package edu.mit.civic.clavin;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.berico.clavin.util.TextUtils;

import edu.mit.civic.clavin.server.ParseManager;

/**
 */
public class ArticleTest {

    /**
     * @throws IOException 
     */
    @Test
    public void testSpeech() throws Exception {
        File inputFile = new File("src/test/resources/sample-docs/speech.txt");
        String inputString = TextUtils.fileToString(inputFile);
        String results = ParseManager.locate(inputString);
        System.out.println(results);
    }
    
}