byte i[6];
byte h;
active proctype FA() {
  i[0] = 'a';
  i[1] = 'a';
  i[2] = 'b';
  i[3] = 'c';
  i[4] = 'c';
  i[5] = '.';
  goto q0;
q0:
  printf("q0\n");
  if
  :: i[h] == 'a' -> printf("%c\n", i[h]); h++; goto q0
  :: i[h] == 'b' -> printf("%c\n", i[h]); h++; goto q1
  :: i[h] == 'b' -> printf("%c\n", i[h]); h++; goto q3
  :: else -> goto reject
  fi;
q1:
  printf("q1\n");
  if
  :: i[h] == 'b' -> printf("%c\n", i[h]); h++; goto q2
  :: else -> goto reject
  fi;
q2:
  printf("q2\n");
  if
  :: i[h] == 'b' -> printf("%c\n", i[h]); h++; goto q1
  :: i[h] == '.' -> goto accept
  :: else -> goto reject
  fi;
q3:
  printf("q3\n");
  if
  :: i[h] == 'c' -> printf("%c\n", i[h]); h++; goto q3
  :: i[h] == '.' -> goto accept
  :: else -> goto reject
  fi;
accept:
  printf("Accepted!\n");
  assert(false);
reject:
    printf("Rejected ...\n")
}
