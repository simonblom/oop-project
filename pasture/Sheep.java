package pasture;
/**
 * Sheep.
 * @author Simon Blom
 */

public class Sheep extends Animal
{
	
	public Sheep(Pasture pasture, int reproduction, int starve, int speed, int sight) 
	{
		super(pasture, "sheep.gif");
		mStarveTimer = starve;
		mReproductionTimer = reproduction;
		mSpeed = speed;
		mSight = sight;
		mMoveDelay = speed;
	}

	@Override
	public boolean isCompatible(Entity otherEntity) 
	{
		if(otherEntity instanceof Grass || otherEntity instanceof Wolf)
			return true;
		else
			return false;
	}
	
	@Override
	public void setMoveDelay(int value)
	{
		mMoveDelay = value;
	}
	
	@Override
	public void setStarveTimer(int value)
	{
		mStarveTimer = value;
	}
}