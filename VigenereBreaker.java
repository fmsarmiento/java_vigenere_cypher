import java.util.*;
import edu.duke.*;

public class VigenereBreaker {
    public String sliceString(String message, int whichSlice, int totalSlices) {
        // message - encrypted message | whichSlice - index of slice | totalSlices - length of the key
        message = message.substring(whichSlice);
        //REPLACE WITH YOUR CODE
        return everyNth(message,totalSlices);
    }
    
    private String everyNth(String str,int nth) {
        String result="";
        for(int k=0;k<str.length();k+=nth) {
            result += str.charAt(k);
        }
        return result;
    }

    public int[] tryKeyLength(String encrypted, int klength, char mostCommon) {
        // message - encrypted msg | klength - length of key | mostCommon - "E"
        int[] key = new int[klength];
        CaesarCracker cc = new CaesarCracker();
        for(int k=0;k<klength;k++) {
            String part = sliceString(encrypted,k,klength);
            key[k] = cc.getKey(part);
        }
        return key;
    }

    public void breakVigenere () {
        CaesarCracker cc = new CaesarCracker();
        FileResource fr = new FileResource();
        int totalchar=0;
        char commonchar = 'E';
        int[] decryptionkey = tryKeyLength(fr.asString(), 4, commonchar);
        System.out.println("Decryption key:");
        for (int elt=0;elt<decryptionkey.length;elt++) {
            System.out.print(decryptionkey[elt]+"\t");
        }
        System.out.println("\nMessage:");
        String[] decrypted = new String[decryptionkey.length];
        for(int k=0;k<decryptionkey.length;k++) {
            String part = sliceString(fr.asString(), k, decryptionkey.length);
            CaesarCipher ci = new CaesarCipher(decryptionkey[k]);
            decrypted[k] = ci.decrypt(part);
            totalchar += decrypted[0].length();
        }
        StringBuilder brokentext = new StringBuilder();
        brokentext.setLength(totalchar);
        String result="";
        for(int j=0;j<decrypted.length;j++) {
            int ctr = j;
            for(int i=0;i<decrypted[j].length();i++) {
                char x = decrypted[j].charAt(i);
                brokentext.setCharAt(ctr,x);
                ctr += decrypted.length;
            }
        }
        System.out.println(brokentext.toString());
        }
        
    public HashSet<String> readDictionary(FileResource fr) {
        HashSet<String> result = new HashSet<String>();
        for (String elt : fr.lines()) {
            result.add(elt);
        }
        return result;
    }
    
    public int countWords(String message, HashSet<String> dictionary) {
        int result=0;
        for (String word : message.split("\\W+")) {
            if(dictionary.contains(word.toLowerCase())) {
                result+=1;
            }
        }
        return result;
    }
    
    public String breakForLanguage(String encrypted, HashSet<String> dictionary, char mostCommon) {
        String result="";
        int max=0;
        int[] keyused = new int[100];
        for(int i=1;i<100;i++) {
            System.out.println("Trying key length "+i+"...");
            int[] keytest = tryKeyLength(encrypted, i, mostCommon);
            VigenereCipher vc = new VigenereCipher(keytest);
            String tryDecrypt = vc.decrypt(encrypted);
            int true_words = countWords(tryDecrypt, dictionary);
            if (i==38) {
            System.out.println("\t Quiz: valid words are "+true_words);
            }
            if (true_words > max) {
                max=true_words;
                result=tryDecrypt;
                keyused = keytest;
            }
        }
        System.out.println("Sensible words is "+max);
        System.out.print("Key used length is "+keyused.length+" and is the ff: ");
        for(int i=0;i<keyused.length;i++) {
            System.out.print(keyused[i] +" ");
        }
        System.out.print("\n\nMessage:\n");
        return result;
    }
    
    public void test_break() {
        FileResource fr = new FileResource("dictionaries/English");
        HashSet<String> dict = readDictionary(fr);
        FileResource fr2 = new FileResource();
        String encrypted = fr2.asString();
        System.out.println(breakForLanguage(encrypted, dict, 'E'));
    }
    
    }
