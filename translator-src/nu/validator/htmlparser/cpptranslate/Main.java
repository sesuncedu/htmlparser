/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is HTML Parser C++ Translator code.
 *
 * The Initial Developer of the Original Code is
 * Mozilla Foundation.
 * Portions created by the Initial Developer are Copyright (C) 2008
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Henri Sivonen <hsivonen@iki.fi>
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package nu.validator.htmlparser.cpptranslate;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class Main {

    private static final String[] H_LIST = {
        "Tokenizer",
        "TreeBuilder",
        "AttributeName",
        "ElementName",
        "HtmlAttributes",
        "NamedCharacters",
        "StackNode",
        "UTF16Buffer",
    };
    
    private static final String[] CPP_LIST = {
        "Tokenizer",
        "TreeBuilder",
        "AttributeName",
        "ElementName",
        "HtmlAttributes",
        "NamedCharacters",
        "Portability",
        "StackNode",
        "UTF16Buffer",
    };
    
    /**
     * @param args
     * @throws ParseException 
     * @throws IOException 
     */
    public static void main(String[] args) throws ParseException, IOException {
        CppTypes cppTypes = new CppTypes();
        
        File javaDirectory = new File(args[0]);
        File cppDirectory = new File(args[1]);
        
        for (int i = 0; i < H_LIST.length; i++) {
            parseFile(cppTypes, javaDirectory, cppDirectory, CPP_LIST[i], ".h", new HVisitor(cppTypes));
        }
        for (int i = 0; i < CPP_LIST.length; i++) {
            parseFile(cppTypes, javaDirectory, cppDirectory, CPP_LIST[i], ".cpp", new CppVisitor(cppTypes));
        }
    }

    private static void parseFile(CppTypes cppTypes, File javaDirectory, File cppDirectory, String className, String fne, CppVisitor visitor) throws ParseException,
            FileNotFoundException, UnsupportedEncodingException, IOException {
        CompilationUnit cu = JavaParser.parse(new NoCppInputStream(new FileInputStream(new File(javaDirectory, className + ".java"))), "utf-8");
        cu.accept(visitor, null);
        FileOutputStream out = new FileOutputStream(new File(cppDirectory, cppTypes.classPrefix() + className + fne));
        OutputStreamWriter w = new OutputStreamWriter(out, "utf-8");
        w.write(visitor.getSource());
        w.close();
    }

}
