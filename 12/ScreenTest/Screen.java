// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/12/Screen.jack

/**
 * A library of functions for displaying graphics on the screen.
 * The Hack physical screen consists of 512 rows (indexed 0..511, top to bottom)
 * of 256 pixels each (indexed 0..255, left to right). The top left pixel on 
 * the screen is indexed (0,0).
 */

class Screen {
	static Array screen, twoToThe;
	static boolean color;
	static int pixel;
	
	function void setTwoToThe() {
		let twoToThe = Array.new(16);
    		let twoToThe[0] = 1; let twoToThe[1] = 2; let twoToThe[2] = 4; let twoToThe[3] = 8;
    		let twoToThe[4] = 16; let twoToThe[5] = 32; let twoToThe[6] = 64; let twoToThe[7] = 128;
    		let twoToThe[8] = 256; let twoToThe[9] = 512; let twoToThe[10] = 1024; let twoToThe[11] = 2048;
    		let twoToThe[12] = 4096; let twoToThe[13] = 8192; let twoToThe[14] = 16384; let twoToThe[15] = 16384 + 16384;
		return;
	}

    /** Initializes the Screen. */
    function void init() {
    	
    	do Screen.setTwoToThe();
    	let screen = 16384;
    	let color = true;
    	//do Screen.clearScreen();
	return;
    }

    /** Erases the entire screen. 
    function void clearScreen() {
    	var int i; 
    	let i = 0;
    	
    	while (i < 8192) {
    		let screen[i] = 0;
		let i = i + 1;
    	}
	return;
    }*/

    /** Sets the current color, to be used for all subsequent drawXXX commands.
     *  Black is represented by true, white by false. */
    function void setColor(boolean b) {
    	let color = b;
	return;
    }

    /** Draws the (x,y) pixel, using the current color. */
    function void drawPixel(int x, int y) {
    	var int address, value;
    	let address = screen + (32 * y) + (x / 16);
    	
    	let value = Memory.peek(address);
    	let pixel = twoToThe[x - (16 * (x / 16))]; //modulo operation implemented locally
    	
    	if (color) { //if color = black
    		let value = value | pixel;
    	}
    	else {				//if color = white
    		let value = value & (~ pixel);
    	}
	do Memory.poke(address, value);
	return;
    }

    /** Draws a line from pixel (x1,y1) to pixel (x2,y2), using the current color. */
    function void drawLine(int x1, int y1, int x2, int y2) {
    	var int x, y, dx, dy, a, b, diff, addr1, addr2, i;
    	
    	if (x2 > x1) { let x = x1; }
		else 		 { let x = x2; }
    	if (y2 > y1) { let y = y1; }
		else	     { let y = y2; }
    	
    	if (x1 = x2) { //special case of straight vertical line
    		let i = Math.abs(y2 - y1);
    		while (i > -1) {
    			do Screen.drawPixel(x, y + i);
    			let i = i - 1;
    		}
    		return;
    	}
    	
    	if (y1 = y2) { //special case of straight horizontal line
    		let i = Math.abs(x2 - x1);
    		while (i > -1) {
    			do Screen.drawPixel(x + i, y);
    			let i = i - 1;
    		}
    		return;
    	}
    	
    	//we make sure that the drawing always goes from left to right, for simplicity
    	
    	if (x2 > x1) { let x = x1; let y = y1;}
		else 		 { let x = x2; let y = y2;}
    	
    	let dx = Math.abs(x2 - x1); let dy = y2 - y1;
    	
    	let a = 0; let b = 0;
    	let diff = a * dy - b * dx;
    	
    	while ( (~(a = dx)) & (~(b = dy)) ) { //while ((a!=dx) & (b!=dy))
    		do Screen.drawPixel(x + a, y + b);
    		
    		if (~(dy < 0)) { //if dy >= 0, or if the direction is downward
    			if (diff < 0) {
    				let a = a + 1;
    				let diff = diff + dy;
    			}
    			else {
    				let b = b + 1;
    				let diff = diff - dx;
    			}
    		}
    		else { //if dy < 0, or if the direction is upward
    			if (diff < 0) {
					let b = b - 1;
					let diff = diff + dx;
				}
				else {
					let a = a + 1;
					let diff = diff + dy;
				}
    		}
    	}
    	return;
    }

    /** Draws a filled rectangle whose top left corner is (x1, y1)
     * and bottom right corner is (x2,y2), using the current color. */
    function void drawRectangle(int x1, int y1, int x2, int y2) {
	var int i, y;
	let i = Math.abs(y2 - y1);

	if (y2 > y1) {
		let y = y1;
	}
	else{
		let y = y2;
	}
	
	while (i > -1){
		do Screen.drawLine(x1, y + i, x2, y + i);
		let i = i - 1;
	}
	return;
    }

    /** Draws a filled circle of radius r<=181 around (x,y), using the current color. */
    function void drawCircle(int cx, int cy, int r) {
    	var int calc1, calc2, dy;
    	let dy = cy - Math.abs(r);
    	
    	while (~(dy = -r)) {
    		//we save these intermediate calculations as to reuse them later for efficiency
    		let calc1 = Math.sqrt((r * r) - (dy * dy));
    		let calc2 = cy + dy;
    		do Screen.drawLine(cx - calc1, calc2, cx + calc1, calc2);
    		let dy = dy - 1;
    	}
    	let calc1 = Math.sqrt((r * r) - (dy * dy));
		let calc2 = cy + dy;
		do Screen.drawLine(cx - calc1, calc2, cx + calc1, calc2);
	return;
    }
}
