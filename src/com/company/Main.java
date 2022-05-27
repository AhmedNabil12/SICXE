package com.company;

import java.io.*;
import java.util.*;

public class Main {

    static ArrayList<String> sym = new ArrayList<>();
    static ArrayList<String> inst = new ArrayList<>();
    static ArrayList<String> ref = new ArrayList<>();
    static ArrayList<String> add = new ArrayList<>();
    static ArrayList<String> obj = new ArrayList<>();
    static ArrayList<String> litL = new ArrayList<>();
    // static ArrayList<String> litEnd = new ArrayList<>();
    // static ArrayList<String> word = new ArrayList<>();
    static String word[] = new String[10];
    static ArrayList<String> v = new ArrayList<>();
    static ArrayList<String> x = new ArrayList<>();
    static String zz;
    static int pc;
    static int ta;
    static int base;
    static int ta2;
    static int y;
    static Boolean flag;


    public static void main(String[] args) {
        // this array is maximum of length 3 because the line
        // consists of symbol, instruction then reference
        String[] s = new String[3];
//read file
        try {
            int i = 0;
            Scanner scanner = new Scanner(new File("D:\\testSic\\inSICXE.txt"));
            while (scanner.hasNextLine()) {
                String r = scanner.nextLine();
                s = r.split("\\t");
                if (s.length == 2) {
                    if (s[0].equals("*")) {
                        sym.add(s[0].trim());
                        inst.add(s[1].trim());
                        ref.add(";");
                    } else {
                        sym.add(";");
                        inst.add(s[0].trim());
                        ref.add(s[1].trim());
                    }
                } else if (s.length == 3) {
                    sym.add(s[0].trim());
                    inst.add(s[1].trim());
                    ref.add(s[2].trim());
                } else if (s.length == 1) {
                    sym.add(";");
                    inst.add(s[0].trim());
                    if (s[0].equals("LTORG")) {
                        ref.add(";");
                    } else {
                        ref.add("0");
                    }
                }


                i++;
            }


            scanner.close();
            checkLit();

            locCounter();
            ObjCode(inst);
          //  print();

            System.out.println("-------------------------------------\nHTE Record\n-------------------------------------");
            H();
            T();
            for (int j = 0; j < sym.size(); j++) {
                if (inst.get(j ).contains("+") && !ref.get(j ).startsWith("#")) {
                    M(j);
                }
            }
            E();

            System.out.println(obj.size());
            for(int x=0;x< obj.size();x++)
            {
                System.out.println(obj.get(x));
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void checkLit()
    {

        int m=0;
        for(int j=0;j< sym.size();j++)
        {
            if(ref.get(j).startsWith("=X") || ref.get(j).startsWith("=C"))
            {
                litL.add(ref.get(j));
            }
        }

        int posLTORG=-1;
        for(int j=0;j< sym.size();j++)
        {
            if(inst.get(j).equals("LTORG"))
            {
                posLTORG= inst.indexOf(inst.get(j));
            }
        }


        if(posLTORG!=1)
        {
            for(int j=posLTORG;j< sym.size();j++)
            {
                if(ref.get(j).startsWith("=X") || ref.get(j).startsWith("=C"))
                {
                    m++;
                }
            }
        }


     for(int x=0;x< sym.size();x++)
     {
            if (inst.get(x).equals("LTORG")) {
                for (int j = litL.size()-m-1; j >=0; j--) {
                    sym.add( x+ 1, "*");
                    inst.add(x + 1, litL.get(j));
                    ref.add(x + 1, ";");
                }
            }
            if (inst.get(x).equals("END")) {
                for (int j = litL.size() - 1; j >= litL.size()-m; j--) {
                    sym.add(x + 1, "*");
                    inst.add(x + 1, litL.get(j));
                    ref.add(x + 1, ";");
                }
            }
        }

        }



    //pass 1//
    public static void locCounter() {


        // first I store the address in int format
        int Hexavalue = Integer.parseInt(ref.get(0), 16);
        // then convert to it hexa and store it in add

        add.add(0, ref.get(0));
        add.add(1, ref.get(0));
        add.add(1, Integer.toHexString(Hexavalue));


        for (int i = 1; i < sym.size(); i++) {
            if (inst.get(i).trim().equals("RESW")) {
                //if it is resw get the ref[i] *3 convert it to int ,
                // then add it to the hexa variable and convert it to hexa
                int j = Integer.parseInt(ref.get(i)) * 3;
                Hexavalue = Hexavalue + j;
                add.add(i + 1, Integer.toHexString(Hexavalue));

            } else if (inst.get(i).trim().equals("RESB")) {
                int j = Integer.parseInt(ref.get(i));
                Hexavalue = Hexavalue + j;
                add.add(i + 1, Integer.toHexString(Hexavalue));


            } else if (sym.get(i).equals("*")) {
                if (inst.get(i).contains("=C")) {
                    int k = inst.get(i).length() - 4;
                    Hexavalue = Hexavalue + k;
                    add.add(i + 1, Integer.toHexString(Hexavalue));

                }

                if (inst.get(i).contains("=X")) {
                    int k = (inst.get(i).length() - 4) / 2;
                    Hexavalue = Hexavalue + k;
                    add.add(i + 1, Integer.toHexString(Hexavalue));

                }
            } else if (inst.get(i).trim().equals("BYTE")) {
                if (ref.get(i).startsWith("C")) {
                    int k = ref.get(i).length() - 3;
                    Hexavalue = Hexavalue + k;
                    add.add(i + 1, Integer.toHexString(Hexavalue));

                }

                if (ref.get(i).startsWith("X")) {
                    int k = (ref.get(i).length() - 3) / 2;
                    Hexavalue = Hexavalue + k;
                    add.add(i + 1, Integer.toHexString(Hexavalue));

                }
            } else if (inst.get(i).trim().startsWith("F") || inst.get(i).trim().endsWith("IO") || inst.get(i).trim().equals("NORM")) {
                Hexavalue = Hexavalue + 1;
                add.add(i + 1, Integer.toHexString(Hexavalue));

            } else if (inst.get(i).trim().endsWith("R") || inst.get(i).trim().equals("RMO") || inst.get(i).trim().equals("SHIFTL") || inst.get(i).trim().equals("SVC")) {
                Hexavalue = Hexavalue + 2;
                add.add(i + 1, Integer.toHexString(Hexavalue));
            } else if (inst.get(i).trim().startsWith("+") || inst.get(i).trim().startsWith("$")) {
                Hexavalue = Hexavalue + 4;
                add.add(i + 1, Integer.toHexString(Hexavalue));

            } /*else if (inst.get(i).trim().startsWith("&")) {
                Hexavalue = Hexavalue + 3;
                add.add(i + 1, Integer.toHexString(Hexavalue));

            } */ else if (inst.get(i).trim().equals("BASE") || inst.get(i).trim().equals("LTORG")) {
                Hexavalue = Hexavalue + 0;
                add.add(i + 1, Integer.toHexString(Hexavalue));
            } else {
                Hexavalue = Hexavalue + 3;
                add.add(i + 1, Integer.toHexString(Hexavalue));
            }

        }


        for (int j = 0; j < sym.size(); j++) {
            System.out.println(sym.get(j) + '\t' + inst.get(j) + '\t' + ref.get(j) + '\t');
        }
    }


    public static void ObjCode(ArrayList<String> inst) {
        //converter c;
        converter.initialize();
        for (int i = 0; i < sym.size(); i++) {
            if (inst.get(i).startsWith("=")) {
                try{
                    if (inst.get(i).contains("=X")) {
                        obj.add(i+1, "");
                        char[] hex = new char[inst.get(i).length()];
                        for (int q = 3; q < inst.get(i).length() ; q++) {
                            hex[q] = inst.get(i).charAt(q);
                            obj.add(i+1, obj.get(i) + hex[q]);

                        }
                    }
                    if (inst.get(i).contains("=C")) {
                        int asciiCode;
                        char[] hex = new char[inst.get(i - 1).length()];
                        obj.add(i+1, "");
                        for (int c = 3; c < inst.get(i).length(); c++) {
                            hex[c] = inst.get(i).charAt(c);
                            asciiCode = (int) hex[c];
                            obj.add(i+1, obj.get(i) + Integer.toString(asciiCode));

                        }
                    }
                }catch(Exception e){}
            }
            switch (inst.get(i).trim()) {

                default:
                    //obj.size
                    for (int xx = 0; xx < sym.size(); xx++) {
                         if (inst.get(i).contains(converter.OPTAB[xx][0]))
                         {
                            int t = Integer.parseInt(converter.OPTAB[xx][2], 16);
                            if (ref.get(i).contains("#")) {
                                t += 1;
                            } else if (ref.get(i).contains("@")) {
                                t += 2;
                            } else {
                                t += 3;
                            }
                            obj.add(i+1, Integer.toHexString(t));
                            String value = "00".substring(obj.get(i).length()) + obj.get(i);
                            obj.add(i+1, value);
                            break;
                        }
                    }
                    break;
                case "BASE":
                    continue;

                case "LTORG":
                    continue;

                case "RESW":
                    continue;

                case "RESB":
                    continue;

                case "BYTE":
                    try {
                        if (ref.get(i).startsWith("X")) {
                            obj.add(i - 1, "");
                            char[] hex = new char[ref.get(i).length()];
                            for (int q = 2; q < ref.get(i).length() - 1; q++) {
                                hex[q] = ref.get(i).charAt(q);
                                obj.add(i +1, obj.get(i) + hex[q]);
                            }
                        }
                    } catch (Exception e) {
                    }
                    try {
                        if (ref.get(i).startsWith("C")) {
                            int asciiCode;
                            char[] hex = new char[ref.get(i).length()];
                            obj.add(i +1, " ");
                            for (int c = 2; c < ref.get(i).length(); c++) {
                                hex[c] = ref.get(i).charAt(c);
                                asciiCode = (int) hex[c];
                                obj.add(i+1, obj.get(i) + Integer.toString(asciiCode));
                            }
                        }
                    } catch (Exception e) {
                    }

                    break;

                case "WORD":
                    int t = Integer.parseInt(ref.get(i));

                    obj.add(i +1, Integer.toHexString(t));
                    String value = "000000".substring(obj.get(i).length()) + obj.get(i);
                    obj.add(i+1, value);
                    break;
            }
        }

        for (int i = 0; i < sym.size(); i++) {
            for (int xx = 0; xx < sym.size(); xx++) {
                if (inst.get(i).contains(converter.OPTAB[xx][0])) {
                    try{
                        String s = converter.OPTAB[xx][1];
                        if (s.equals("2") && !inst.get(i).startsWith("+")) {
                            obj.add(i +1, converter.OPTAB[xx][2]);
                            format2(i);
                            break;
                        } else if (s.equals("3") && !inst.get(i).startsWith("+")) {
                            check(i);
                            format3(i);
                            break;
                        } else if (inst.get(i).startsWith("+")) {
                            check(i);
                            format4(i);
                            break;
                        }
                    }catch (Exception e){}
                }
            }
        }
    }

    public static void format4(int i) {
        if (flag == false) {
            if (ref.get(i - 1).contains("#")) {
                String replace = ref.get(i).replace("#", "");
                x.add(1, "0");
                x.add(2, "0");
                x.add(3, "1");
                if (ref.get(i).contains(",X")) {
                    x.add(0, "1");
                } else {
                    x.add(0, "0");
                }
                int t = Integer.parseInt(replace);
                String g = Integer.toHexString(t);
                String value = "00000".substring(g.length()) + g;
                String code2 = x.get(0) + x.get(1) + x.get(2) + x.get(3);
                int cc = Integer.parseInt(code2, 2);
                String code = Integer.toHexString(cc);
                obj.add(i+1, obj.get(i) + code + value);

            } else {
                x.add(1, "0");
                x.add(2, "0");
                x.add(3, "1");
                if (ref.get(i).contains(",X")) {
                    x.add(0, "1");
                } else {
                    x.add(0, "0");

                }
                int t = Integer.parseInt(ref.get(i), 16);
                String g = Integer.toHexString(t);
                String value = "00000".substring(g.length()) + g;
                String code2 = x.get(0) + x.get(1) + x.get(2) + x.get(3);
                int cc = Integer.parseInt(code2, 2);
                String code = Integer.toHexString(cc);
                obj.add(i +1, obj.get(i) + code + value);
                //obj[i-1] += code + value;
            }
        } else {
            try
            {
                x.add(1, "0");
                x.add(2, "0");
                x.add(3, "1");

                if (ref.get(i - 1).contains(",X")) {
                    x.add(0, "1");
                } else {
                    x.add(0, "0");
                }
                for (int p = 1; p < sym.size(); p++) {
                    if (ref.get(i).contains(sym.get(p))) {
                        zz = add.get(p);
                        y = Integer.parseInt(zz, 16);

                    }
                }
                String dis = Integer.toHexString(y);
                String value = "00000".substring(dis.length()) + dis;
                String code2 = x.get(0) + x.get(1) + x.get(2) + x.get(3);
                int cc = Integer.parseInt(code2, 2);
                String code = Integer.toHexString(cc);
                obj.add(i+1, obj.get(i) + code + value);
                //obj[i-1] += code + value;
            } catch (Exception e) {
            }
        }
    }

    public static void format3(int i) {

        if (flag == false) {
            try
            {
                if (ref.get(i).contains("#")) {
                    x.add(1, "0");
                    x.add(2, "0");
                    x.add(3, "0");


                    if (ref.get(i).contains(",X")) {
                        x.add(0, "1");
                    } else {
                        x.add(0, "0");
                    }
                    String replace = ref.get(i).replace("#", "");
                    int t = Integer.parseInt(replace);
                    String g = Integer.toHexString(t);
                    String value = "000".substring(g.length()) + g;
                    String code2 = x.get(0) + x.get(1) + x.get(2) + x.get(3);
                    int cc = Integer.parseInt(code2, 2);
                    String code = Integer.toHexString(cc);
                    obj.add(i+1, obj.get(i) + code + value);

                } else {
                    try {
                        x.add(1, "0");
                        x.add(2, "0");
                        x.add(3, "0");

                        if (ref.get(i).contains(",X")) {
                            x.add(0, "1");
                        } else {
                            x.add(0, "0");
                        }
                        int t = Integer.parseInt(ref.get(i), 16);
                        String g = Integer.toHexString(t);
                        String value = "000".substring(g.length()) + g;
                        String code2 = x.get(0) + x.get(1) + x.get(2) + x.get(3);
                        int cc = Integer.parseInt(code2, 2);
                        String code = Integer.toHexString(cc);
                        obj.add(i +1, obj.get(i) + code + value);
                    } catch (Exception e) {
                    }

                }
            }catch(Exception e){

            }
        }

        else {
            try {
                x.add(1, "0");
                x.add(2, "1");
                x.add(3, "0");

                if (ref.get(i).contains(",X")) {
                    x.add(0, "1");
                } else {
                    x.add(0, "0");
                }
                if (ref.get(i).contains("=C")) {
                    for (int z = 0; z < sym.size(); z++) {
                        if (ref.get(i).contains(inst.get(z))) {
                            pc = Integer.parseInt(add.get(i), 16);
                            ta = Integer.parseInt(add.get(z), 16);
                            // System.out.println(add[z-1]);
                        }
                    }
                } else {
                    for (int z = 0; z < sym.size(); z++) {
                        if (ref.get(i).contains(sym.get(z))) {
                            pc = Integer.parseInt(add.get(i), 16);
                            ta = Integer.parseInt(add.get(z), 16);
                        }
                    }
                }
                int disp = ta - pc;
                // System.out.println(disp);
                if (-2048 < disp && disp < 2047) {

                    String dis = Integer.toHexString(disp);
                    if (dis.length() > 3) {
                        dis = dis.substring(dis.length() - 3);
                    }
                    if (inst.get(i).equals("LTORG")) {
                        obj.add(i+1, ";");
                    } else {
                        String value = "000".substring(dis.length()) + dis;
                        String code2 = x.get(0) + x.get(1) + x.get(2) + x.get(3);
                        int cc = Integer.parseInt(code2, 2);
                        String code = Integer.toHexString(cc);
                        obj.add(i +1, obj.get(i) + code + value);

                    }
                } else {

                    x.add(1, "1");
                    x.add(2, "0");
                    for (int o = 0; o < sym.size(); o++) {
                        if (inst.get(o).trim().equals("BASE")) {
                            for (int z = 0; z < sym.size(); z++) {
                                if (ref.get(o).contains(sym.get(z))) {
                                    base = Integer.parseInt(add.get(z));
                                    for (int j = 0; j < sym.size(); j++) {
                                        if (ref.get(i).contains(sym.get(j))) {
                                            ta2 = Integer.parseInt(add.get(j));
                                        }
                                    }

                                }
                            }
                        }
                    }
                    int disp2 = ta2 - base;
                    String dis = Integer.toHexString(disp2);
                    String value = "000".substring(dis.length()) + dis;
                    String code2 = x.get(0) + x.get(1) + x.get(2) + x.get(3);
                    int cc = Integer.parseInt(code2, 2);
                    String cod = Integer.toHexString(cc);
                    String code = "000".substring(cod.length()) + cod;
                    if (code.length() > 1) {
                        code = code.substring(2);
                    }
                    obj.add(i+1, obj.get(i) + code + value);
                }
            } catch (Exception e) {
            }
        }
    }

    public static void format2 ( int i) {
        String j = ref.get(i);
        if (j.contains(",")) {
            word = j.split(",");
            obj.add(i + 1, obj.get(i) + " ");
            for (int l = 0; l < 2; l++) {
                switch (word[l]) {
                    case "A":
                        obj.add(i+1, obj.get(i) + 0);
                        break;
                    case "X":
                        obj.add(i+1, obj.get(i) + 1);
                        break;
                    case "L":
                        obj.add(i+1, obj.get(i) + 2);
                        break;
                    case "B":
                        obj.add(i+1, obj.get(i) + 3);
                        break;
                    case "S":
                        obj.add(i+1, obj.get(i) + 4);
                        break;
                    case "T":
                        obj.add(i+1, obj.get(i) + 5);
                        break;
                    case "F":
                        obj.add(i+1, obj.get(i ) + 6);
                        break;
                }
            }
        } else {
            int y = 0;

            word[y] = j;
            obj.add(i +1, obj.get(i) + "");
            for (int l = 0; l < 1; l++) {
                switch (word[l]) {
                    case "A":
                        obj.add(i+1, obj.get(i) + "00");
                        break;
                    case "X":
                        obj.add(i+1, obj.get(i) + 10);
                        break;
                    case "L":
                        obj.add(i+1, obj.get(i) + 20);
                        break;
                    case "B":
                        obj.add(i+1, obj.get(i) + 30);
                        break;
                    case "S":
                        obj.add(i+1, obj.get(i) + 40);
                        break;
                    case "T":
                        obj.add(i+1, obj.get(i) + 50);
                        break;
                    case "F":
                        obj.add(i+1, obj.get(i) + 60);
                        break;
                }
            }
        }

    }

    public static void check ( int i){
        for (int j = 0; j < sym.size(); j++) {
            if (ref.get(i).contains(sym.get(j))) {
                flag = true;
                break;
            } else {
                flag = false;
            }
        }

    }

    public static void H () {
        int flag;
        String len;
        flag = Integer.parseInt(add.get(45), 16) - Integer.parseInt(add.get(1), 16);
        len = Integer.toHexString(flag);
        String value = "000000".substring(add.get(1).length()) + add.get(1);
        add.add(1, value);
        // add[1] = value;
        System.out.println("H ^ " + sym.get(0) + " ^ " + add.get(1) + " ^ " + len);
    }

    public static void T () {
        int flag;
        int flag1;
        String len;
        String len1;
        int count = 1;

        while (!inst.get(count).contains("RES")) {
            count++;
        }
        flag = Integer.parseInt(add.get(count), 16) - Integer.parseInt(add.get(1), 16);
        len = Integer.toHexString(flag);
        System.out.print("T ^ " + add.get(1) + " ^ " + len + " ^ ");
        for (int i = 0; i < count; i++) {
            try {
                System.out.print(obj.get(i));
            }catch (Exception e){}
        }

        while (inst.get(count).contains("RES")) {
            count++;
        }
        System.out.println();
        int newCount = count;

        while (!inst.get(count).contains("RES") && count < newCount + 25) {
            count++;
        }
        flag1 = Integer.parseInt(add.get(count), 16) - Integer.parseInt(add.get(newCount), 16);
        len1 = Integer.toHexString(flag1);
        String value = "000000".substring(add.get(newCount).length()) + add.get(newCount);
        add.add(newCount, value);
        // add[newCount] = value;
        System.out.print("T ^ " + add.get(newCount) + " ^ " + len1 + " ^ ");
        for (int i = newCount; i < count; i++) {
            try{
                System.out.print(obj.get(i));
            }catch (Exception e){}
        }

        while (inst.get(count).contains("RES")) {
            count++;
        }
        System.out.println();
        newCount = count;
    }

    public static void M ( int j){
        int m = Integer.parseInt(add.get(j), 16);
        m += 1;
        add.add(j+1, Integer.toHexString(m));
        //add[j-1] = Integer.toHexString(m);
        String value = "000000".substring(add.get(j).length()) + add.get(j);
        add.add(j+1, value);
        //add[j-1] = value;
        System.out.println("M ^ " + add.get(j) + "^ 05 ^ " + "+" + sym.get(0));
    }

    public static void E () {
        String value = "000000".substring(add.get(1).length()) + add.get(1);
        add.add(1, value);
        // add[1] = value;
        System.out.println("E ^ " + add.get(1));
    }


    public static void print () {
            for (int i = 0; i < sym.size(); i++)
            {
                String value = "0000".substring(add.get(i).length()) + add.get(i);
                if (inst.get(i).trim().equalsIgnoreCase("base") || inst.get(i).trim().equalsIgnoreCase("ltorg"))
                {
                    System.out.println(sym.get(i) + '\t' + inst.get(i) + '\t' + ref.get(i) + '\t' );
                }
                else
                {
                    System.out.println(value.toUpperCase() + sym.get(i) + '\t'  + inst.get(i) + '\t' + ref.get(i) +"\t"  );
                }
            }

            System.out.println("--------------------------------------------------------------");
            System.out.println("--------------------------------------------------------------");
            System.out.println("-----------------------\nSymbol Table\n-----------------------");
            System.out.println("--------------------------------------------------------------");
            System.out.println("--------------------------------------------------------------");

            System.out.println("Symb" + "\t" + "LocCtr");
            for (int i = 0; i < sym.size(); i++) {
                String value = "0000".substring(add.get(i).length()) + add.get(i);

                if (!sym.get(i).contains(";") && !sym.get(i).contains("*")) {
                    System.out.println(sym.get(i) + "\t" + value.toUpperCase());
                }
            }


            System.out.println("--------------------------------------------------------------");
            System.out.println("--------------------------------------------------------------");
            System.out.println("-----------------------\nLiteral Table\n-----------------------");
            System.out.println("--------------------------------------------------------------");
            System.out.println("Inst" + "\t" + "objcode" + "\t" + "added");
            for (int i = 1; i < sym.size(); i++) {
                if (sym.get(i).contains("*")) {
                    int nw = Integer.parseInt(add.get(i + 1), 16) - Integer.parseInt(add.get(i), 16);
                    String ss = Integer.toHexString(nw);
                    System.out.println(inst.get(i) + "\t" + obj.get(i) + "\t" + ss);
                }
            }

            FileWriter myWriter = null;
            try {
                myWriter = new FileWriter("D:\\testSic\\SICXEoutput.txt");
                PrintWriter pw = new PrintWriter(myWriter);

                for (int i = 0; i < sym.size(); i++) {
                    String value = "0000".substring(add.get(i).length()) + add.get(i);
                    if (inst.get(i).trim().equalsIgnoreCase("base") || inst.get(i).trim().equalsIgnoreCase("ltorg")) {
                        pw.println(sym.get(i) + '\t' + inst.get(i) + '\t' + ref.get(i) + '\t');
                    } else {
                        pw.println(value.toUpperCase() + '\t' + sym.get(i) + '\t' + inst.get(i) + '\t' + ref.get(i) + '\t');
                    }
                }
                pw.println("-----------------------\nSymbol Table\n-----------------------");
                for (int i = 0; i < sym.size(); i++) {
                    String value = "0000".substring(add.get(i).length()) + add.get(i);

                    if (!sym.get(i).contains(";") && !sym.get(i).contains("*")) {
                        pw.println(sym.get(i) + "\t" + value.toUpperCase());
                    }
                }
                pw.close();
            } catch (
                    Exception e) {
                e.printStackTrace();
            }
        }

}

