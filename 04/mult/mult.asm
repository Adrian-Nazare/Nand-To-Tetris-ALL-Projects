// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

// Put your code here.

//assign value contained in R0 to variable a
@R0
D=M
@a
M=D


//assign value contained in R1 to variable b
@R1
D=M
@b
M=D

//initialize sum contained in R2 to 0
@R2
M=0

//initialize coutner i with 0
@i
M=0

(LOOP)
//compare to be, and if it reached the value of b, jump to END
@i
D=M
@b
D=D-M
@END
D;JGE

//increment the sum with a
@a
D=M
@R2
M=D+M

//increment coutner i, then jump back to loop
@i
M=M+1
@LOOP
0;JMP

(END)
@END
0;JMP











