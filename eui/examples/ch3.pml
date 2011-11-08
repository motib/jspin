chan ch1 = [5] of { byte, byte, byte };
byte a, b, c;

active proctype p() {
  ch1 !  1, 2, 3;
  ch1 !  4, 5, 6;
  ch1 !  7, 8, 9;
  ch1 !  10, 11, 12;

  ch1 ?? 4, b, c;
  printf("%d %d\n", b, c);

  c = 12;
  ch1 ?? a, b, eval(c);
  printf("%d %d\n", a, b);

  c = 9;
  ch1 ?? <7, b, eval(c)>;
  printf("%d\n", b);
}
