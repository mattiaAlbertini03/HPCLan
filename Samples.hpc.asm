move SP CL 
pushr CL 
move SP AL 
pushr AL 

storei A0 5
pushr A0 
storei A0 5
pushr A0 
storei A0 0
pushr A0 
pushr A0 
pushr A0 
pushr A0 
pushr A0 
storei A0 0
pushr A0 
storei A0 0
pushr A0 
push function0

storei A0 5
move AL T1
subi T1 8
load A0 0(T1) 
label5:
move AL T1 
subi T1 8
store A0 0(T1) 
subi A0 1 
move AL T1
subi T1 8
load A0 0(T1) 
move AL T1 
subi T1 8
store A0 0(T1) 
pushr A0 
storei A0 0
popr T1 
blt T1 A0 label4
pushr CL 
move AL T1
pushr T1 
move AL T1 
subi T1 8
store A0 0(T1) 
pushr A0
move SP CL 
addi CL 1
addi CL 2
move CL AL 
subi AL 1 
jsub function0
pushr A0 
move AL T1 
subi T1 2
subi T1 1 
pushr T1 
move AL T1 
subi T1 8
store A0 0(T1) 
popr T1 
sub T1 A0 
popr T1 
popr A0 
load A0 0(T1) 
b label5 
label4:
label7:
move AL T1 
subi T1 9
store A0 0(T1) 
pushr A0 
move AL T1 
subi T1 1
store A0 0(T1) 
popr T1 
blt T1 A0 label8
storei A0 0 
b label9
label8:
storei A0 1
label9:
storei T1 0 
beq A0 T1 label6
move AL T1 
subi T1 2
subi T1 1 
pushr T1 
storei A0 0
popr T1 
sub T1 A0 
popr T1 
store A0 0(T1) 
pushr A0 
move AL T1 
subi T1 2
subi T1 1 
pushr T1 
move AL T1 
subi T1 9
store A0 0(T1) 
popr T1 
sub T1 A0 
popr T1 
store A0 0(T1) 
popr T1 
add T1 A0 
popr A0 
pushr A0 
move AL T1 
subi T1 2
subi T1 1 
pushr T1 
storei A0 0
popr T1 
sub T1 A0 
popr T1 
popr A0 
load A0 0(T1) 
move AL T1 
subi T1 9
store A0 0(T1) 
pushr A0 
storei A0 1
popr T1 
add T1 A0 
popr A0 
move AL T1
subi T1 9
load A0 0(T1) 
b label7 
label6:
move AL T1 
subi T1 2
subi T1 1 
pushr T1 
storei A0 0
popr T1 
sub T1 A0 
popr T1 
store A0 0(T1) 
halt


function0:
pushr RA 
move AL T1 
subi T1 1
store A0 0(T1) 
pushr A0 
storei A0 0
popr T1 
beq A0 T1 label2
storei A0 0
b label3
label2:
storei A0 1
label3:
storei T1 1 
beq A0 T1 label0
move AL T1 
subi T1 1
store A0 0(T1) 
pushr A0 
pushr CL 
move AL T1
store T1 0(T1) 
pushr T1 
move AL T1 
subi T1 1
store A0 0(T1) 
pushr A0 
storei A0 1
popr T1 
sub T1 A0 
popr A0 
pushr A0
move SP CL 
addi CL 1
addi CL 2
move CL AL 
subi AL 1 
jsub function0
popr T1 
mul T1 A0 
popr A0 
b label1
label0:
storei A0 1
label1:
addi SP 0
popr RA 
addi SP 1
pop 
store CL 0(CL) 
move CL AL 
subi AL 1 
pop 
rsub RA 

