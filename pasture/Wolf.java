package pasture;
/**
 * Wolf.
 * @author Simon Blom
 */

public class Wolf extends Animal
{
	public Wolf(Pasture pasture, int reproduction, int starve, int speed, int sight) 
	{
		super(pasture, "wolf.gif");
		mStarveTimer = starve;
		mReproductionTimer = reproduction;
		mSpeed = speed;
		mSight = sight;
		mMoveDelay = speed;
	}

	@Override
	public boolean isCompatible(Entity otherEntity) 
	{
		if(otherEntity instanceof Grass || otherEntity instanceof Sheep)
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
