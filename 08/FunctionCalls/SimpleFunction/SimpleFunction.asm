// function SimpleFunction.test 2
(FUNCTION_SimpleFunction.test)
@SP
M=M+1
A=M-1
M=0
@SP
M=M+1
A=M-1
M=0

// push local 0
@0
D=A
@LCL
A=D+M
D=M
@SP
M=M+1
A=M-1
M=D

// push local 1
@1
D=A
@LCL
A=D+M
D=M
@SP
M=M+1
A=M-1
M=D

// add
@SP
AM=M-1
D=M
A=A-1
M=D+M

// neg
@SP
A=M-1
M=!M

// push argument 0
@0
D=A
@ARG
A=D+M
D=M
@SP
M=M+1
A=M-1
M=D

// add
@SP
AM=M-1
D=M
A=A-1
M=D+M

// push argument 1
@1
D=A
@ARG
A=D+M
D=M
@SP
M=M+1
A=M-1
M=D

// sub
@SP
AM=M-1
D=M
A=A-1
M=M-D

// return
// FRAME=LCL
@LCL
D=M
@FRAME
M=D
// RET=*(FRAME-5)
@5
A=D-A
D=M
@RET
M=D
// *ARG=pop()
@SP
A=M-1
D=M
@ARG
A=M
M=D
// SP=ARG+1
D=A+1
@SP
M=D
// THAT=*(FRAME-1)
@FRAME
AM=M-1
D=M
@THAT
M=D
// THIS=*(FRAME-2)
@FRAME
AM=M-1
D=M
@THIS
M=D
// ARG=*(FRAME-3)
@FRAME
AM=M-1
D=M
@ARG
M=D
// LCL=*(FRAME-4)
@FRAME
AM=M-1
D=M
@LCL
M=D
// goto RET
@RET
A=M
0;JMP

