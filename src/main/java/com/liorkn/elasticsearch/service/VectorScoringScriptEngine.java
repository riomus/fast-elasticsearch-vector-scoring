package com.liorkn.elasticsearch.service;

import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.script.ScoreScript;
import org.elasticsearch.script.ScriptContext;
import org.elasticsearch.script.ScriptEngine;
import org.elasticsearch.script.SearchScript;

import java.util.Map;

/**
 * Created by Lior Knaany on 5/14/17.
 */
public class VectorScoringScriptEngine extends AbstractComponent implements ScriptEngine {

    public static final String NAME = "knn";

    @Inject
    public VectorScoringScriptEngine(Settings settings) {
        super(settings);
    }

    @Override
    public String getType() {
        return NAME;
    }

    @Override
    public <FactoryType> FactoryType compile(String s, String s1, ScriptContext<FactoryType> scriptContext, Map<String, String> map) {
        ScoreScript.Factory factory = VectorScoringScriptFactory::new;

        return scriptContext.factoryClazz.cast(factory);
    }
//
//    @Override
//    public Object compile(String scriptName, String scriptSource, Map<String, String> params) {
//        return new VectorScoreScript.Factory();
//    }
//
//
//    @Override
//    public boolean isInlineScriptEnabled() {
//        return true;
//    }
//
//    @Override
//    public String getType() {
//        return NAME;
//    }
//
//    @Override
//    public <FactoryType> FactoryType compile(String s, String s1, ScriptContext<FactoryType> scriptContext, Map<String, String> map) {
//        return null;
//    }
//
//    @Override
//    public String getExtension() {
//        return NAME;
//    }
//
//    @Override
//    public ExecutableScript executable(CompiledScript compiledScript, @Nullable Map<String, Object> vars) {
//        VectorScoreScript.Factory scriptFactory = (VectorScoreScript.Factory) compiledScript.compiled();
//        return scriptFactory.newScript(vars);
//    }
//
//    @Override
//    public SearchScript search(CompiledScript compiledScript, final SearchLookup lookup, @Nullable final Map<String, Object> vars) {
//        final VectorScoreScript.Factory scriptFactory = (VectorScoreScript.Factory) compiledScript.compiled();
//        final VectorScoreScript script = (VectorScoreScript) scriptFactory.newScript(vars);
//        return new SearchScript() {
//            @Override
//            public LeafSearchScript getLeafSearchScript(LeafReaderContext context) throws IOException {
//                script.setBinaryEmbeddingReader(context.reader().getBinaryDocValues(script.field));
//                return script;
//            }
//            @Override
//            public boolean needsScores() {
//                return scriptFactory.needsScores();
//            }
//        };
//    }
//
//    @Override
//    public void close() {
//    }
}
