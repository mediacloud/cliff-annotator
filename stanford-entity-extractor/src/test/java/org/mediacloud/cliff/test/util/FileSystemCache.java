package org.mediacloud.cliff.test.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileSystemCache {

    private static final Logger logger = LoggerFactory.getLogger(FileSystemCache.class);

    private File tempDir;
    
    public FileSystemCache(String cacheName){
        tempDir = new File(System.getProperty("java.io.tmpdir"),cacheName);
        if(!tempDir.exists()){
            tempDir.mkdir();
            logger.info("Created cache at "+tempDir.getAbsolutePath());
        } else {
            logger.info("Existing cache at "+tempDir.getAbsolutePath());
        }
    }
    
    public boolean put(String rawKey, String value){
        try {
            FileWriter fw = new FileWriter(getFile(rawKey).getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(value);
            bw.close();
            logger.debug("Cached "+rawKey+" to "+getFile(rawKey).getAbsolutePath());
            return true;
        } catch (IOException e) {
            logger.error("Couldn't write to file "+e.toString());
        }
        return false;
    }
    
    public String get(String rawKey){
        if(!contains(rawKey)) return null;
        byte[] encoded;
        try {
            encoded = Files.readAllBytes(Paths.get(getFile(rawKey).getAbsolutePath()));
            return new String(encoded, StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("Couldn't read file "+getFile(rawKey).getAbsolutePath());
        }
        return null;
    }
    
    private File getFile(String rawKey){
        String key = md5(rawKey);
        return new File(tempDir.getAbsolutePath(),key);
    }
    
    public boolean contains(String rawKey){
        return getFile(rawKey).exists();
    }

    public void delete(String rawKey){
        if(contains(rawKey)) getFile(rawKey).delete();
    }

    public static String md5(String rawKey) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            byte[] data = rawKey.getBytes(); 
            m.update(data,0,data.length);
            BigInteger i = new BigInteger(1,m.digest());
            return String.format("%1$032X", i);
        } catch (NoSuchAlgorithmException nsae){
            logger.error("Dan't find MD5 algorithm :-(");
        }
        return "";
    }
    
    public static void main(String[] args) throws Exception {
        FileSystemCache cache = new FileSystemCache("test-cache");
        String key = "testing123";
        String value = "this is some content";
        cache.delete(key);
        cache.put(key,value);
        String results = cache.get(key);
        org.junit.Assert.assertEquals(value, results);
    }
    
}
