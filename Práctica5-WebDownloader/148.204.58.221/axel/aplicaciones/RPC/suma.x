struct sumandos
{
	int sumando1;
	int sumando2;
};
program SUMA_PRG {
	version SUMA_VER {
		int suma (sumandos) = 1;
	} = 1;
} = 0x20000001;
