package sample;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

class DES{
    private static int sizeOfBlock = 128;
    private static int sizeOfChar = 16;

    private static int shiftKey = 2;

    static int quantityOfRounds = 16;

    static String[] Blocks;

    public static String StringToRightLength(String input) {
        while (((input.length() * sizeOfChar) % sizeOfBlock) != 0)
            input += "#";

        return input;
    }

    public static void CutStringIntoBlocks(String input)
    {
        Blocks = new String[(input.length() * sizeOfChar) / sizeOfBlock];

        int lengthOfBlock = input.length() / Blocks.length;

        for (int i = 0; i < Blocks.length; i++)
        {
            Blocks[i] = input.substring(i * lengthOfBlock, i * lengthOfBlock + lengthOfBlock);
            Blocks[i] = StringToBinaryFormat(Blocks[i]);
        }
    }

    public static String  StringToBinaryFormat(String input){
        StringBuilder output = new StringBuilder();
        char[] chars = input.toCharArray();
        for (char aChar : chars) {
            output.append(
                    String.format("%8s", Integer.toBinaryString(aChar))   // char -> int, auto-cast
                            .replaceAll(" ", "0")                         // zero pads
            );
        }

        return output.toString();
    }

    public static String CorrectKeyWord(String input, int lengthKey)
    {
        if (input.length() > lengthKey)
            input = input.substring(0, 0 + lengthKey);
        else
            while (input.length() < lengthKey)
                input = "0" + input;

        return input;
    }

    public static String EncodeDES_One_Round(String input, String key)
    {
        String L = input.substring(0, input.length() / 2);
        String R = input.substring(input.length() / 2, input.length() / 2 + input.length() / 2);

        return (R + XOR(L, f(R, key)));
    }

    public static String DecodeDES_One_Round(String input, String key)
    {
        String L = input.substring(0, input.length() / 2);
        String R = input.substring(input.length() / 2, input.length() / 2 + input.length() / 2);

        return (XOR(f(L, key), R) + L);
    }

    private static String XOR(String s1, String s2) {
        String result = "";
        char[] chars1 = s1.toCharArray();
        char[] chars2 = s2.toCharArray();
        for (int i = 0; i < s1.length(); i++)
        {
            boolean a = false;
            boolean b = false;
            if(chars1[i] == '1'){
                a = true;
            }
            if(chars2[i] == '1'){
                b = true;
            }
            if (a ^ b)
                result += "1";
            else
                result += "0";
        }
        return result;
    }

    private static String f(String s1, String s2)
    {
        return XOR(s1, s2);
    }

    public static String KeyToNextRound(String key)
    {
        char[] keyChars = key.toCharArray();
        for (int i = 0; i < shiftKey; i++)
        {
            key = keyChars[key.length()- 1] + key;
            key = key.substring(0, key.length() - 1);
        }

        return key;
    }

    public static String KeyToPrevRound(String key)
    {
        char[] keyChars = key.toCharArray();
        for (int i = 0; i < shiftKey; i++)
        {
            key = key + keyChars[0];
            key = key.substring(1);
        }
        return key;
    }

    public static String StringFromBinaryToNormalFormat(String input){
        String output = "";
        while (input.length() > 0){
            String Binary = input.substring(0, sizeOfChar);
            input = input.substring(sizeOfChar, input.length());

            int a = 0;
            int degree = Binary.length() - 1;
            char[] charBinary = Binary.toCharArray();
            for (int i = 0; i < Binary.length(); i++){
                a += Integer.parseInt(String.valueOf(charBinary[i]), 2) * Math.pow(2, degree--);
            }
            //int charCode = Integer.parseInt(input, 2);
            output += new Character((char)a).toString();
        }
        return  output;
    }

}

public class Controller {

    @FXML
    private Button encrypt_btn;

    @FXML
    private TextArea input_textarea;

    @FXML
    private Label output;

    @FXML
    private Button AES_btn;

    @FXML
    private TextField keyTextField;

    @FXML
    void initialize() {

        encrypt_btn.setOnAction(actionEvent -> {
            String message = input_textarea.getText();
            String key = keyTextField.getText();
            message = DES.StringToRightLength(message);
            DES.CutStringIntoBlocks(message);
            key = DES.CorrectKeyWord(key, message.length() / (2 * DES.Blocks.length));
            keyTextField.setText(key);
            key = DES.StringToBinaryFormat(key);
            for (int j = 0; j < DES.quantityOfRounds; j++)
            {
                for (int i = 0; i < DES.Blocks.length; i++)
                    DES.Blocks[i] = DES.EncodeDES_One_Round(DES.Blocks[i], key);
                key = DES.KeyToNextRound(key);
            }

            key = DES.KeyToPrevRound(key);
            System.out.println(DES.StringFromBinaryToNormalFormat(key));

            String result = "";
            for (int i = 0; i < DES.Blocks.length; i++){
                result += DES.Blocks[i];
            }
            result = DES.StringFromBinaryToNormalFormat(result);
            output.setText(result);
            System.out.println(result);
        });

        AES_btn.setOnAction(actionEvent -> {
            String message = input_textarea.getText();
            String key = keyTextField.getText();
            key = DES.StringToBinaryFormat(key);
            message = DES.StringToBinaryFormat(message);
            DES.CutStringIntoBlocks(message);
            for (int j = 0; j < DES.quantityOfRounds; j++)
            {
                for (int i = 0; i < DES.Blocks.length; i++)
                    DES.Blocks[i] = DES.DecodeDES_One_Round(DES.Blocks[i], key);
                key = DES.KeyToPrevRound(key);
            }

            key = DES.KeyToNextRound(key);
            System.out.println(DES.StringFromBinaryToNormalFormat(key));

            String result = "";
            for (int i = 0; i < DES.Blocks.length; i++){
                result += DES.Blocks[i];
                DES.Blocks[i] = "";
            }
            result = DES.StringFromBinaryToNormalFormat(result);
            output.setText(result);
            System.out.println(result);
        });
    }
}