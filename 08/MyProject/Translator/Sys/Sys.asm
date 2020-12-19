// function Sys.init 0
(FUNCTION_Sys.Sys.init)

// push constant 4000
@4000
D=A
@SP
M=M+1
A=M-1
M=D

// pop pointer 0
@SP
AM=M-1
D=M
@THIS
M=D

// push constant 5000
@5000
D=A
@SP
M=M+1
A=M-1
M=D

// pop pointer 1
@SP
AM=M-1
D=M
@THAT
M=D

// call Sys.main 0
// push return-address
@RETURN_Sys.Sys.main:0
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
@FUNCTION_Sys.Sys.main
0;JMP
(RETURN_Sys.Sys.main:0)

// pop temp 1
@SP
AM=M-1
D=M
@6
M=D

(LABEL_Sys.Sys.init:LOOP)

@LABEL_Sys.Sys.init:LOOP
0;JMP

// function Sys.main 5
(FUNCTION_Sys.Sys.main)
@SP
M=M+1
A=M-1
M=0
@SP
M=M+1
A=M-1
M=0
@SP
M=M+1
A=M-1
M=0
@SP
M=M+1
A=M-1
M=0
@SP
M=M+1
A=M-1
M=0

// push constant 4001
@4001
D=A
@SP
M=M+1
A=M-1
M=D

// pop pointer 0
@SP
AM=M-1
D=M
@THIS
M=D

// push constant 5001
@5001
D=A
@SP
M=M+1
A=M-1
M=D

// pop pointer 1
@SP
AM=M-1
D=M
@THAT
M=D

// push constant 200
@200
D=A
@SP
M=M+1
A=M-1
M=D

// pop local 1
@1
D=A
@LCL
D=D+M
@addr
M=D
@SP
AM=M-1
D=M
@addr
A=M
M=D

// push constant 40
@40
D=A
@SP
M=M+1
A=M-1
M=D

// pop local 2
@2
D=A
@LCL
D=D+M
@addr
M=D
@SP
AM=M-1
D=M
@addr
A=M
M=D

// push constant 6
@6
D=A
@SP
M=M+1
A=M-1
M=D

// pop local 3
@3
D=A
@LCL
D=D+M
@addr
M=D
@SP
AM=M-1
D=M
@addr
A=M
M=D

// push constant 123
@123
D=A
@SP
M=M+1
A=M-1
M=D

// call Sys.add12 1
// push return-address
@RETURN_Sys.Sys.add12:1
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
@FUNCTION_Sys.Sys.add12
0;JMP
(RETURN_Sys.Sys.add12:1)

// pop temp 0
@SP
AM=M-1
D=M
@5
M=D

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

// push local 2
@2
D=A
@LCL
A=D+M
D=M
@SP
M=M+1
A=M-1
M=D

// push local 3
@3
D=A
@LCL
A=D+M
D=M
@SP
M=M+1
A=M-1
M=D

// push local 4
@4
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

// add
@SP
AM=M-1
D=M
A=A-1
M=D+M

// add
@SP
AM=M-1
D=M
A=A-1
M=D+M

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

// function Sys.add12 0
(FUNCTION_Sys.Sys.add12)

// push constant 4002
@4002
D=A
@SP
M=M+1
A=M-1
M=D

// pop pointer 0
@SP
AM=M-1
D=M
@THIS
M=D

// push constant 5002
@5002
D=A
@SP
M=M+1
A=M-1
M=D

// pop pointer 1
@SP
AM=M-1
D=M
@THAT
M=D

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

// push constant 12
@12
D=A
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

