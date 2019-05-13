package com.mod.loan.util;

import java.util.Random;

public class RandomUtils {
	public final static String randStr = "0123456789abcdefghijklmnopqrstuvwxyz"; // 写入你所希望的所有的字母A-Z,a-z,0-9
	public final static String randNumStr = "123456789"; // 
	
	public static StringBuffer GeneratePassword(int length) {
		StringBuffer generateRandStr = new StringBuffer();
		Random rand = new Random();
		int randStrLength = length; // 接收需要生成随机数的长度
		for (int i = 0; i < randStrLength; i++) {
			int randNum = rand.nextInt(36);
			generateRandStr.append(randStr.substring(randNum, randNum + 1));
		}
		return generateRandStr; // 返回生成的随机数
	}

	public static String generateRandomNum(int length) {
		StringBuffer generateRandStr = new StringBuffer();
		Random rand = new Random();
		int randStrLength = length; // 接收需要生成随机数的长度
		for (int i = 0; i < randStrLength; i++) {
			int randNum = rand.nextInt(9);
			generateRandStr.append(randNumStr.substring(randNum, randNum + 1));
		}
		return generateRandStr.toString(); // 返回生成的随机数
	}
	
	/*
	 *生成指定位数的数字字符串 
	 */
	public static String generateConfirmStr(String str) {
		int a = str.length();
		int b= 6 - a;
		System.out.println(b);
		for(int i=0;i<=b-1;i++) {
			str = "0" + str;
		}
		return str;
		
	}

	public static void main(String[] args) {
		//String str = generateConfirmStr("8187779");
		StringBuffer aa = GeneratePassword(32);
		System.out.println(aa);
	}
}
