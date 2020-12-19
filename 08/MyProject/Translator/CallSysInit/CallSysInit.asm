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

