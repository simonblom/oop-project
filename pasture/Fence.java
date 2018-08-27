package pasture;
/**
 * Fence.
 * @author Simon Blom
 */

public class Fence extends Idle
{
	public Fence(Pasture pasture)
	{
		super(pasture, "fence.gif");
	}
	
	@Override
	public void tick() 
	{
		// Does nothing
	}

	@Override
	public boolean isCompatible(Entity otherEntity) 
	{
		// Cannot be crossed
		return false;
	}

	@Override
	public void die() 
	{
		// Cannot die
	}
}
