package Obfuscator;

import Parser.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Trees;

import java.io.*;
import java.lang.System;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


/**
 * Created by hatin on 2017/1/10.
 */
public class Main {

    public static String childTokenString(ParseTree ctx)
    {
        if (ctx == null)
            return "";

        int nChildren = ctx.getChildCount();

        if (nChildren == 0)
        {
            return ctx.getText();
        }

        String retval = "";

        for (int i = 0; i < nChildren; i++)
        {
            ParseTree child = ctx.getChild(i);
            String childText = childTokenString(child);
            if (!childText.equals(""))
            {
                retval += childText + " ";
            }
        }

        if (retval.length() > 0)
            retval = retval.substring(0, retval.length() - 1);
        return retval;
    }
    public static void main(String[] args) throws IOException {
        String f = "D:\\JAVALab\\JAVAObfuscator\\InFile\\HelloWorld.java";
        Lexer lexer = new Java8Lexer(new ANTLRFileStream(f));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        Java8Parser parser = new Java8Parser(tokens);
        ParserRuleContext t = parser.compilationUnit();

        ArrayList<ParseTree> VDID = (ArrayList<ParseTree>) Trees.findAllRuleNodes(t, Java8Parser.RULE_variableDeclaratorId);
        Map<String, String> map = new HashMap<String, String>();

        for (ParseTree i : VDID) {
            ParserRuleContext ii = (ParserRuleContext) i;
            String va = ii.getText();
            map.put(ii.getText(), ObTech(i.getText()));
            ii.removeLastChild();
            CommonToken ct = new CommonToken(Java8Parser.RULE_variableDeclaratorId, map.get(va));
            ii.addChild(ct);
        }
        ArrayList<ParseTree> EN = (ArrayList<ParseTree>) Trees.findAllRuleNodes(t, Java8Parser.RULE_expressionName);
        for (ParseTree i : EN) {
            ParserRuleContext ii = (ParserRuleContext) i;
            String va = ii.getText();
            ii.removeLastChild();
            CommonToken ct = new CommonToken(Java8Parser.RULE_expressionName, map.get(va));
            ii.getParent().addChild(ct);
        }

        Save(childTokenString(t),"D:\\JAVALab\\JAVAObfuscator\\OutFile\\HelloWorld.java");
    }

    private static void Save(String s, String s1) {
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(s1), "utf-8"));
            writer.write(s);
        } catch (IOException ex) {
            // report
        } finally {
            try {writer.close();} catch (Exception ex) {/*ignore*/}
        }
    }

    private static String md5(String plaintext) {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        m.reset();
        m.update(plaintext.getBytes());
        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1, digest);
        String hashtext = bigInt.toString(16);
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }
        return hashtext;
    }

    private static String ObTech(String text) {
        String pre = "ASSET";
        Random ra = new Random();
        int ri = Math.abs(ra.nextInt()) % 20;
        String mid = md5(text).substring(ri, ri + 12);
        return (pre + mid);
    }

}
