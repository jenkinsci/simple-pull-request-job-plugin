package io.jenkins.plugins.sprp;

import com.iwombat.util.StringUtil;
import org.apache.commons.lang.StringUtils;

public class PipelineSnippetGenerator {
    PipelineSnippetGenerator(){
    }

    public String shellScritp(String path){
        return
                "script {\n" +
                "\tif (isUnix()) {\n" +
                "\t\tsh '"+ path + ".sh" + "'\n" +
                "\t} else {\n" +
                "\t\tbat '"+ path + ".bat" + "'\n" +
                "\t}\n" +
                "}";
    }

    // This function will add tabs at the beginning of each line
    public String addTabs(String script, int numberOfTabs){
        String tabs = StringUtils.repeat("\t", numberOfTabs);

        script = script.replace("\n", "\n" + tabs);
        return script;
    }

    public String getTabString(int number){
        return StringUtils.repeat("\t", number);
    }

    //TODO: Change to support full specs
    public String getAgent(){
        return "any";
    }
}
