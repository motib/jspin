chan ch1 = [2] of { byte, byte, byte};
chan ch2 = [0] of { byte, byte, byte};
byte a, b, c;

active proctype p() {
  ch1 ! 1, 2, 3;
  ch1 ! 4, 5, 6;
  ch2 ! 7, 8, 9;
  ch2 ! 10, 11, 12;
}

active proctype q() {
  ch1 ? a, b, c;
  printf("%d %d %d\n", a, b, c);
  ch1 ? a, b, c;
  printf("%d %d %d\n", a, b, c);
  ch2 ? a, b, c;
  printf("%d %d %d\n", a, b, c);
  ch2 ? a, b, c;
  printf("%d %d %d\n", a, b, c);
}
