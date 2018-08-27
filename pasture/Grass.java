package pasture;
import java.awt.Point;

/**
 * Grass.
 * @author Simon Blom
 */

public class Grass extends Idle
{
	private int mReproductionTimer;
	
	public Grass(Pasture pasture, int reproduction) 
	{
		super(pasture, "plant.gif");
		mReproductionTimer = reproduction;
	}

	@Override
	public void tick() 
	{
		act();
	}
	
	public void act()
	{
		if(alive())
			breed();
	}
	
	private void breed()
	{
		Pasture pasture = getPasture();
		if(mReproductionTimer == 0)
		{
			Point safePosition = pasture.getSafePosition(this);
			if(safePosition != null)
			{
				Entity offspring = new Grass(pasture, pasture.reproductionTimerGrass);
            	pasture.addEntity(offspring, safePosition);
			}
			mReproductionTimer = pasture.reproductionTimerGrass;
		}
		else
			mReproductionTimer--;
	}
	
	public void die()
	{
		getPasture().removeEntity(this);
	}

	@Override
	public boolean isCompatible(Entity otherEntity) {
		if(otherEntity instanceof Wolf || otherEntity instanceof Sheep)
			return true;
		else
			return false;
	}
}
