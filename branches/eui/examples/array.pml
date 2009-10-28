byte  a[10] = 20;
byte  n = 0;

init {
  do
  :: n < 10 -> printf("%d\n", a[n]); n++
  :: else   -> break
  od;
  n = 0;
  do
  :: n < 10 -> a[n] = n; n++
  :: else   -> break
  od;
  n = 10;
  do
  :: n > 0  -> printf("%d\n", a[n-1]); n--
  :: else   -> break
  od;
}
