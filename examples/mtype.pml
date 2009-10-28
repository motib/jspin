mtype = { request, reply, ack };
mtype m1, m2, m3;

chan ch1 = [5] of { mtype, byte, byte};
mtype a;
byte  b, c;

active proctype p() {
  m1 = ack;
  m2 = reply;
  m3 = request;
  printf("%e %e %e\n", m1, m2, m3);

  ch1 !  request, 2, 3;
  ch1 !  reply, 4, 5;
  ch1 !  ack, 6, 7;
  ch1 !! reply, 8, 9;
  ch1 !! ack, 10, 11;

  ch1 ? a, b, c;
  printf("%e %d %d\n", a, b, c);
  ch1 ? a, b, c;
  printf("%e %d %d\n", a, b, c);
  ch1 ? <a, b, c>;
  printf("%e %d %d\n", a, b, c);
  ch1 ? a, b, c;
  printf("%e %d %d\n", a, b, c);
  ch1 ? a, b, c;
  printf("%e %d %d\n", a, b, c);
  ch1 ? a, b, c;
  printf("%e %d %d\n", a, b, c);
}
