package net.yakuprising.ld29;

public interface CollisionData {
	ObjectType GetType();
	void HandleBeginContact(CollisionData other);
}
