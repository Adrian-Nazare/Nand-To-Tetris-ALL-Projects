// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/12/Memory.jack

/**
 * This library provides two services: direct access to the computer's main
 * memory (RAM), and allocation and recycling of memory blocks. The Hack RAM
 * consists of 32,768 words, each holding a 16-bit binary number.
 */ 
class Memory {
	static Array ram, heap, freeList;

    /** Initializes the class. */
    function void init() {
    	let ram = 0;
    	let heap = 2048; //heap base
    	let freeList = heap;
    	let heap[0] = 0; //next (freeList.next)
    	let heap[1] = 14335; //length (freeList.size)
    }

    /** Returns the RAM value at the given address. */
    function int peek(int address) {
    	return ram[address];
    }

    /** Sets the RAM value at the given address to the given value. */
    function void poke(int address, int value) {
    	let ram[address] = value;
    }

    /** Finds an available RAM block of the given size and returns
     *  a reference to its base address. */
    function int alloc(int size) {
    	var Array block;
    	let block = freeList;
    	
    	while ((size + 2) > block[1]) {
    		let block = block[0];
    		if (block = 0) {
    			return false;
    		}
    	}
    	let block[1] = block[1] - (size + 2); //reduce the size of the old block
    	let block = block + 2 + block[1]; //base address of the new block
    	let block[0] = 0; //null pointer
    	let block[1]] = size; //size of the new block
    	return block + 2;
    }

    /** De-allocates the given object (cast as an array) by making
     *  it available for future allocations. */
    function void deAlloc(Array o) {
    	let heap[o - 2] = freeList; //append the freeList start to the "next" of the newly released block
    	freeList = address - 2; //set the new freeList beginning to this address;
    }    
}
