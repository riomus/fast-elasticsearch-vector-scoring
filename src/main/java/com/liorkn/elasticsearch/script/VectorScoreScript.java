/*
Based on: https://discuss.elastic.co/t/vector-scoring/85227/4
and https://github.com/MLnick/elasticsearch-vector-scoring

another slower implementation using strings: https://github.com/ginobefun/elasticsearch-feature-vector-scoring

storing arrays is no luck - lucine index doesn't keep the array members orders
https://www.elastic.co/guide/en/elasticsearch/guide/current/complex-core-fields.html

Delimited Payload Token Filter: https://www.elastic.co/guide/en/elasticsearch/reference/2.4/analysis-delimited-payload-tokenfilter.html


 */

package com.liorkn.elasticsearch.script;

import com.liorkn.elasticsearch.Util;
import com.liorkn.elasticsearch.script.metrics.*;
import org.apache.lucene.index.BinaryDocValues;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.store.ByteArrayDataInput;
import org.elasticsearch.script.ScoreScript;
import org.elasticsearch.search.lookup.LeafDocLookup;
import org.elasticsearch.search.lookup.SearchLookup;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Script that scores documents based on cosine similarity embedding vectors.
 */
public final class VectorScoreScript extends ScoreScript {

    public static final String SCRIPT_NAME = "binary_vector_score";

    private static final int DOUBLE_SIZE = 8;

    // the field containing the vectors to be scored against
    public final String field;
    private final SearchLookup lookup;
    private final LeafReaderContext leafContext;
    private  BinaryDocValues binaryValues;
    int docId ;
    @Override
    public void setDocument(int docId) {
        this.docId = docId;
    }

    private final double[] inputVector;


    private final HashMap<String, MetricProvider> metrics = new HashMap<String, MetricProvider>(){{
        put("cosine", new CosineMetricProvider());
        put("dot", new DotMetricProvider());
        put("tsss", new TSSSMetricProvider());
        put("euclidean", new EuclideanMetricProvider());
    }};

    private final Metric metric;


    /**
     * Init
     * @param params index that a scored are placed in this parameter. Initialize them here.
     */
    @SuppressWarnings("unchecked")
    public VectorScoreScript(Map<String, Object> params, SearchLookup lookup, LeafReaderContext leafContext) {
        super(params, lookup, leafContext);
        this.lookup = lookup;
        this.leafContext = leafContext;
        final Object metricConf = params.get("metric");
        MetricProvider metricProvider = metricConf != null ?
                metrics.get(metricConf) :
                metrics.get("dot");
        final Object field = params.get("field");
        if (field == null)
            throw new IllegalArgumentException("binary_vector_score script requires field input");
        this.field = field.toString();
        try {
            this.binaryValues = this.leafContext.reader().getBinaryDocValues(this.field);
        } catch (Exception e){
            throw new RuntimeException("Failed to get binary doc values" , e);
        }

        // get query inputVector - convert to primitive
        final Object vector = params.get("vector");
        if(vector != null) {
            final ArrayList<Double> tmp = (ArrayList<Double>) vector;
            inputVector = new double[tmp.size()];
            for (int i = 0; i < inputVector.length; i++) {
                inputVector[i] = tmp.get(i);
            }
        } else {
            final Object encodedVector = params.get("encoded_vector");
            if(encodedVector == null) {
                throw new IllegalArgumentException("Must have at 'vector' or 'encoded_vector' as a parameter");
            }
            inputVector = Util.convertBase64ToArray((String) encodedVector);
        }

        metric = metricProvider.getMetric(inputVector);
    }


    /**
     * Called for each document
     * @return cosine similarity of the current document against the input inputVector
     */
    @Override
    public final double execute() {
        final int size = inputVector.length;
        try{
            this.binaryValues.advance(docId);
            if(this.binaryValues.docID() != this.docId) {
                throw new RuntimeException("Got doc "+this.binaryValues.docID()+" expected "+docId);
            }
            byte[] bytes = this.binaryValues.binaryValue().bytes;
            LeafDocLookup leafDocLookup = this.lookup.doc().getLeafDocLookup(this.leafContext);
            final ByteArrayDataInput input = new ByteArrayDataInput(bytes);
            input.readVInt(); // returns the number of values which should be 1, MUST appear hear since it affect the next calls
            final int len = input.readVInt(); // returns the number of bytes to read
            if(len != size * DOUBLE_SIZE) {
                return 0.0;
            }
            final int position = input.getPosition();
            final DoubleBuffer doubleBuffer = ByteBuffer.wrap(bytes, position, len).asDoubleBuffer();

            final double[] docVector = new double[size];
            doubleBuffer.get(docVector);

            double score = metric.metric(docVector);
            return score;
        } catch (Exception e){
            throw new RuntimeException("Failed to get binary doc values" , e);
        }

    }




}