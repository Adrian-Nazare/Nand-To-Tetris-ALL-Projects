// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/12/Sys.jack

/**
 * A library that supports various program execution services.
 */
class Sys {

    /** Performs all the initializations required by the OS. */
    function void init() {
    	do Math.init();
    	do Memory.init();
    	do Screen.init();
    	do Output.init();
    	do Keyboard.init();
    	
    	do Main.main();
    	do Sys.halt();
    	return;
    }

    /** Halts the program execution. */
    function void halt() {
    	while (true) {}
    	return;
    }

    /** Waits approximately duration milliseconds and returns.  */
    function void wait(int duration) {
    	var int i, j, k;
    	let i = 0; let j = 0; let k = 0;
    	while (i < (10 * duration)) {
    		while (j < 32767) {
    			while (k < 32767) {
    				let k = k + 1;
        		}
    			let j = j + 1;
    		}
    		let i = i + 1;
    	}
    	return;
    }

    /** Displays the given error code in the form "ERR<errorCode>",
     *  and halts the program's execution. */
    function void error(int errorCode) {
    	do String.printString("ERR<");
    	do String.printInt("errorCode");
    	do String.printString(">");
    	do Sys.halt();
    	return;
    }
}
