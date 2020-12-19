// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/12/String.jack

/**
 * Represents character strings. In addition for constructing and disposing
 * strings, the class features methods for getting and setting individual
 * characters of the string, for erasing the string's last character,
 * for appending a character to the string's end, and more typical
 * string-oriented operations.
 */
class String {
	field Array str;
	field int length;

    /** constructs a new empty string with a maximum length of maxLength
     *  and initial length of 0. */
    constructor String new(int maxLength) {
    	if (maxLength = 0) {
    		let str = 0; //null pointer
    	}
    	else {
    		let str = Array.new(maxLength);
    	}    	
    	let length = 0;
    	return this;
    }

    /** Disposes this string. */
    method void dispose() {
    	do Memory.deAlloc(this);
    	return;
    }

    /** Returns the current length of this string. */
    method int length() {
    	return length;
    }

    /** Returns the character at the j-th location of this string. */
    method char charAt(int j) {
    	return str[j];
    }

    /** Sets the character at the j-th location of this string to c. */
    method void setCharAt(int j, char c) {
    	let str[j] = c;
    	return;
    }

    /** Appends c to this string's end and returns this string. */
    method String appendChar(char c) {
    	let str[length] = c;
    	let length = length + 1;
    	return this;
    }

    /** Erases the last character from this string. */
    method void eraseLastChar() {
    	let length = length - 1;
    	return;
    }

    /** Returns the integer value of this string, 
     *  until a non-digit character is detected. */
    method int intValue() {
    	var int seed, i;
    	var char c;
    	let seed = 0;
    	
    	while (i < length) {
    		let c = str[i];
    		if ((c < 48) | (c > 57)) { //if character is non-numeric
    			return seed;
    		}
    		else {
    			let seed = (seed * 10) + (c - 48);
    		}
    	}
    	return seed;
    }

    /** Sets this string to hold a representation of the given value. */
    method void setInt(int val) {
    	let length = 0;
    	do actuallySetInt(val);
    	return;
    }
    
    /*setInt code set within a private method: due to it being called recursively, we want to make sure that the length is reset to 0 only once*/
    method void actuallySetInt(int val) {
    	var int divided;
    	
    	if (val < 10) {
    		if (val > -1) { // if a single digit between and including 0 and 9
    			do appendChar(val + 48);
    		}
    		else { //if the number is negative
    			do appendChar(45); //append '-'
    			do actuallySetInt(Math.abs(val));
    		}
    	}
    	else { //number is a positive two-digit numebr or greater
    		let divided = val / 10;
    		do actuallySetInt(divided);
    		do actuallySetInt(val - (divided * 10));
    	}
    	return;
    }

    /** Returns the new line character. */
    function char newLine() {
    	return 128;
    }

    /** Returns the backspace character. */
    function char backSpace() {
    	return 129;
    }

    /** Returns the double quote (") character. */
    function char doubleQuote() {
    	return 34;
    }
}
