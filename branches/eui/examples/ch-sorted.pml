chan ch1 = [5] of { byte, byte, byte};
byte a, b, c;

active proctype p() {
  ch1 !  4, 5, 6;
  ch1 !  7, 8, 9;
  ch1 !  1, 2, 3;
  ch1 !! 7, 8, 8;
  ch1 !! 20, 21, 22;

  ch1 ? a, b, c;
  printf("%d %d %d\n", a, b, c);
  ch1 ? a, b, c;
  printf("%d %d %d\n", a, b, c);
  ch1 ? <a, b, c>;
  printf("%d %d %d\n", a, b, c);
  ch1 ? a, b, c;
  printf("%d %d %d\n", a, b, c);
  ch1 ? a, b, c;
  printf("%d %d %d\n", a, b, c);
  ch1 ? a, b, c;
  printf("%d %d %d\n", a, b, c);
}
