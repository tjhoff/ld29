package net.yakuprising.ld29;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class GameContactListener implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		// TODO Auto-generated method stub
		CollisionData a = (CollisionData)contact.getFixtureA().getBody().getUserData();
		CollisionData b = (CollisionData)contact.getFixtureB().getBody().getUserData();
		
		a.HandleBeginContact(b);
		b.HandleBeginContact(a);
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub

	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub

	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub

	}

}
