byte pos[4];
byte step[4];


active proctype P_0() { 
byte j=0;
byte k=0;

NCS: if
:: j = 1; goto wait; 

fi;
CS: if
:: pos[0] = 0; goto NCS; 

fi;
wait: if
::  d_step {j<4;pos[0] = j;}  goto q2; 

:: j==4; goto CS; 

fi;
q2: if
::  d_step {step[j-1] = 0;k = 0;}  goto q3; 

fi;
q3: if
::  d_step {k<4 && (k==0 || pos[k]<j);k = k+1;}  goto q3; 

::  d_step {pos[j-1]!=0 || k==4;j = j+1;}  goto wait; 

fi;
}

active proctype P_1() { 
byte j=0;
byte k=0;

NCS: if
:: j = 1; goto wait; 

fi;
CS: if
:: pos[1] = 0; goto NCS; 

fi;
wait: if
::  d_step {j<4;pos[1] = j;}  goto q2; 

:: j==4; goto CS; 

fi;
q2: if
::  d_step {step[j-1] = 1;k = 0;}  goto q3; 

fi;
q3: if
::  d_step {k<4 && (k==1 || pos[k]<j);k = k+1;}  goto q3; 

::  d_step {pos[j-1]!=1 || k==4;j = j+1;}  goto wait; 

fi;
}

active proctype P_2() { 
byte j=0;
byte k=0;

NCS: if
:: j = 1; goto wait; 

fi;
CS: if
:: pos[2] = 0; goto NCS; 

fi;
wait: if
::  d_step {j<4;pos[2] = j;}  goto q2; 

:: j==4; goto CS; 

fi;
q2: if
::  d_step {step[j-1] = 2;k = 0;}  goto q3; 

fi;
q3: if
::  d_step {k<4 && (k==2 || pos[k]<j);k = k+1;}  goto q3; 

::  d_step {pos[j-1]!=2 || k==4;j = j+1;}  goto wait; 

fi;
}

active proctype P_3() { 
byte j=0;
byte k=0;

NCS: if
:: j = 1; goto wait; 

fi;
CS: if
:: pos[3] = 0; goto NCS; 

fi;
wait: if
::  d_step {j<4;pos[3] = j;}  goto q2; 

:: j==4; goto CS; 

fi;
q2: if
::  d_step {step[j-1] = 3;k = 0;}  goto q3; 

fi;
q3: if
::  d_step {k<4 && (k==3 || pos[k]<j);k = k+1;}  goto q3; 

::  d_step {pos[j-1]!=3 || k==4;j = j+1;}  goto wait; 

fi;
}

