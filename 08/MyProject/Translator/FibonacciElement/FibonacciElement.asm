// BootstrapCode: Initialize SP pointer to 256 per convention & call Sys.init
@256
D=A
@SP
M=D
// call Sys.init 0
// push return-address
@RETURN_Sys.init:0
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
@FUNCTION_Sys.init
0;JMP
(RETURN_Sys.init:0)

// function Main.fibonacci 0
(FUNCTION_Main.fibonacci)

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

// push constant 2
@2
D=A
@SP
M=M+1
A=M-1
M=D

// lt
@SP
AM=M-1
D=M
A=A-1
D=M-D
M=-1
@TRUE0
D;JLT
@SP
A=M-1
M=0
(TRUE0)

//if-goto if_true
@SP
AM=M-1
D=M
@LABEL_Main.fibonacci:if_true
D;JNE

//goto if_false
@LABEL_Main.fibonacci:if_false
0;JMP

// label if_true
(LABEL_Main.fibonacci:if_true)

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

// push constant 0
@0
D=A
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

// call Main.fibonacci 1
// push return-address
@RETURN_Main.fibonacci:1
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
@6
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
@FUNCTION_Main.fibonacci
0;JMP
(RETURN_Main.fibonacci:1)

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

// push constant 0
@0
D=A
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

// call Main.fibonacci 1
// push return-address
@RETURN_Main.fibonacci:2
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
@6
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
@FUNCTION_Main.fibonacci
0;JMP
(RETURN_Main.fibonacci:2)

// add
@SP
AM=M-1
D=M
A=A-1
M=D+M

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

// function Main.fibonacci 0
(FUNCTION_Main.fibonacci)

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

// push constant 2
@2
D=A
@SP
M=M+1
A=M-1
M=D

// lt
@SP
AM=M-1
D=M
A=A-1
D=M-D
M=-1
@TRUE1
D;JLT
@SP
A=M-1
M=0
(TRUE1)

//if-goto if_true
@SP
AM=M-1
D=M
@LABEL_Main.fibonacci:if_true
D;JNE

//goto if_false
@LABEL_Main.fibonacci:if_false
0;JMP

// label if_true
(LABEL_Main.fibonacci:if_true)

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

// label if_false
(LABEL_Main.fibonacci:if_false)

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

// push constant 2
@2
D=A
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

// call Main.fibonacci 1
// push return-address
@RETURN_Main.fibonacci:3
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
@6
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
@FUNCTION_Main.fibonacci
0;JMP
(RETURN_Main.fibonacci:3)

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

// push constant 1
@1
D=A
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

// call Main.fibonacci 1
// push return-address
@RETURN_Main.fibonacci:4
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
@6
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
@FUNCTION_Main.fibonacci
0;JMP
(RETURN_Main.fibonacci:4)

// add
@SP
AM=M-1
D=M
A=A-1
M=D+M

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

// push constant 4
@4
D=A
@SP
M=M+1
A=M-1
M=D

// call Main.fibonacci 1
// push return-address
@RETURN_Main.fibonacci:5
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
@6
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
@FUNCTION_Main.fibonacci
0;JMP
(RETURN_Main.fibonacci:5)

// label while
(LABEL_Sys.init:while)

//goto while
@LABEL_Sys.init:while
0;JMP

// Infinite loop at the end of the program
(END)
@END
0;JMP
