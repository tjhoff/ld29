package net.yakuprising.ld29;

public enum ObjectType 
{
	Static (0x01),
	Player (0x02),
	Enemy (0x04),
	Crate (0x08),
	AmmoPickup (0x10),
	BatteryPickup (0x20),
	HealthPickup (0x40);
	
	short bitmask;
	
	ObjectType(int mask)
	{
		bitmask = (short)mask;
	}
}
