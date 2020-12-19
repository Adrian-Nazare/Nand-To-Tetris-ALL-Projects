// function Class1.set 0
(FUNCTION_Class1.set)

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

// pop static 0
@SP
AM=M-1
D=M
@Class1.0
M=D

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

// pop static 1
@SP
AM=M-1
D=M
@Class1.1
M=D

// push constant 0
@0
D=A
@SP
M=M+1
A=M-1
M=D

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

// function Class1.get 0
(FUNCTION_Class1.get)

// push static 0
@Class1.0
D=M
@SP
M=M+1
A=M-1
M=D

// push static 1
@Class1.1
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

// function Class2.set 0
(FUNCTION_Class2.set)

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

// pop static 0
@SP
AM=M-1
D=M
@Class2.0
M=D

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

// pop static 1
@SP
AM=M-1
D=M
@Class2.1
M=D

// push constant 0
@0
D=A
@SP
M=M+1
A=M-1
M=D

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

// function Class2.get 0
(FUNCTION_Class2.get)

// push static 0
@Class2.0
D=M
@SP
M=M+1
A=M-1
M=D

// push static 1
@Class2.1
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

// function Sys.init 0
(FUNCTION_Sys.init)

// push constant 6
@6
D=A
@SP
M=M+1
A=M-1
M=D

// push constant 8
@8
D=A
@SP
M=M+1
A=M-1
M=D

// call Class1.set 2
// push return-address
@RETURN_Class1.set:0
D=A
@SP
M=M+1
A=M-1
M=D
// push LCL
@LCL
D=M
@SP
M=M+1
A=M-1
M=D
// push ARG
@ARG
D=M
@SP
M=M+1
A=M-1
M=D
// push THIS
@THIS
D=M
@SP
M=M+1
A=M-1
M=D
// push THAT
@THAT
D=M
@SP
M=M+1
A=M-1
M=D
// ARG=SP-n-5
@7
D=A
@SP
D=M-D
@ARG
M=D
// LCL=SP
@SP
D=M
@LCL
M=D
// goto f
@FUNCTION_Class1.set
0;JMP
(RETURN_Class1.set:0)

// pop temp 0
@SP
AM=M-1
D=M
@5
M=D

// push constant 23
@23
D=A
@SP
M=M+1
A=M-1
M=D

// push constant 15
@15
D=A
@SP
M=M+1
A=M-1
M=D

// call Class2.set 2
// push return-address
@RETURN_Class2.set:1
D=A
@SP
M=M+1
A=M-1
M=D
// push LCL
@LCL
D=M
@SP
M=M+1
A=M-1
M=D
// push ARG
@ARG
D=M
@SP
M=M+1
A=M-1
M=D
// push THIS
@THIS
D=M
@SP
M=M+1
A=M-1
M=D
// push THAT
@THAT
D=M
@SP
M=M+1
A=M-1
M=D
// ARG=SP-n-5
@7
D=A
@SP
D=M-D
@ARG
M=D
// LCL=SP
@SP
D=M
@LCL
M=D
// goto f
@FUNCTION_Class2.set
0;JMP
(RETURN_Class2.set:1)

// pop temp 0
@SP
AM=M-1
D=M
@5
M=D

// call Class1.get 0
// push return-address
@RETURN_Class1.get:2
D=A
@SP
M=M+1
A=M-1
M=D
// push LCL
@LCL
D=M
@SP
M=M+1
A=M-1
M=D
// push ARG
@ARG
D=M
@SP
M=M+1
A=M-1
M=D
// push THIS
@THIS
D=M
@SP
M=M+1
A=M-1
M=D
// push THAT
@THAT
D=M
@SP
M=M+1
A=M-1
M=D
// ARG=SP-n-5
@5
D=A
@SP
D=M-D
@ARG
M=D
// LCL=SP
@SP
D=M
@LCL
M=D
// goto f
@FUNCTION_Class1.get
0;JMP
(RETURN_Class1.get:2)

// call Class2.get 0
// push return-address
@RETURN_Class2.get:3
D=A
@SP
M=M+1
A=M-1
M=D
// push LCL
@LCL
D=M
@SP
M=M+1
A=M-1
M=D
// push ARG
@ARG
D=M
@SP
M=M+1
A=M-1
M=D
// push THIS
@THIS
D=M
@SP
M=M+1
A=M-1
M=D
// push THAT
@THAT
D=M
@SP
M=M+1
A=M-1
M=D
// ARG=SP-n-5
@5
D=A
@SP
D=M-D
@ARG
M=D
// LCL=SP
@SP
D=M
@LCL
M=D
// goto f
@FUNCTION_Class2.get
0;JMP
(RETURN_Class2.get:3)

// label WHILE
(LABEL_Sys.init:WHILE)

//goto WHILE
@LABEL_Sys.init:WHILE
0;JMP

