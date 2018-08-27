package pasture;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.ImageIcon;

/**
 * Animal.
 * @author Simon Blom
 */

public abstract class Animal implements Entity
{
	protected int mReproductionTimer, mStarveTimer, mSpeed, mSight, mMoveDelay;
	protected boolean mHasEaten = false;
	private Pasture mPasture;
	private ImageIcon mImageIcon;
	private Point mLastPos = null;
	
	public Animal(Pasture pasture, String imagePath)
	{
		mPasture = pasture;
		mImageIcon = new ImageIcon(imagePath);
	}
	
	public Pasture getPasture()
	{
		return mPasture;
	}
	
	@Override
	public ImageIcon getImage() 
	{
		return mImageIcon;
	}
	
	@Override
	public void tick() 
	{	
		hunger();
		if(alive())
			act();
	}
	
	public void act()
	{
		move();
		eat();
		breed();
	}
	
	public boolean alive()
	{
		if(mPasture.getEntities().contains(this))
			return true;
		else
			return false;
	}
	
	public void die()
	{
		mPasture.removeEntity(this);
	}
	
	public void eat()
	{
		Collection<Entity> entities = mPasture.getEntitiesAt(mPasture.getPosition(this));
		if(entities != null)
		{
			for(Entity e : entities)
			{
				if(isCompatible(e))
				{
					if(this instanceof Sheep && e instanceof Grass)
					{
						setStarveTimer(mPasture.starveTimerSheep);
						e.die();
						this.mHasEaten = true;
					}
					if(this instanceof Wolf && e instanceof Sheep)
					{
						setStarveTimer(mPasture.starveTimerWolf);
						e.die();
						this.mHasEaten = true;
					}
				}
			}
		}
	}
	
    /**
     * Moves the entity.
     */
	public void move()
	{
		mMoveDelay--;
		if(mMoveDelay == 0)
		{
			Point destination;
			List<Point> points = mPasture.getFreeNeighbors(this);
			Entity closestEnemy = getClosestEnemyInSight(this);
			Entity closestFood = getClosestFoodInSight(this);
			destination = getEvaluatedPoint(points, closestEnemy, closestFood);
			
			if(destination == null && mLastPos != null)
				destination = getSimilarPoint(points);
			if(destination == null)
				destination = mPasture.getRandomMember(points);
			if(destination != null)
			{
				mLastPos = mPasture.getPosition(this);
				mPasture.moveEntity(this, destination);
			}
			mMoveDelay = mSpeed;
		}
	}
    
    /**
     * Returns closest food in sight to given entity.
     */
    public Entity getClosestFoodInSight(Entity entity)
    {
    	Entity closest = null;
    	List<Entity> list = mPasture.getEntitiesInSight(mPasture.getPosition(entity), mSight);
    	if(list !=null)
    	{
    		for(Entity e : list)
    		{
    			double distance = mPasture.getPosition(entity).distance(mPasture.getPosition(e));
    			if(edible(e))
    			{
    				if(closest == null || distance <= mPasture.getPosition(entity).distance(mPasture.getPosition(closest)))
    				{
    					closest = e;
    				}
    			}

    		}
    	}
    	return closest;
    }
    
	private boolean edible(Entity food)
	{
		if(this instanceof Sheep && food instanceof Grass)
		{
			return true;
		}
		else if(this instanceof Wolf && food instanceof Sheep)
		{
			return true;
		}
		else
			return false;
	}
    
    /**
     * Returns closest enemy in sight to given entity.
     */
    public Entity getClosestEnemyInSight(Entity entity)
    {
    	Entity closest = null;
    	List<Entity> list = mPasture.getEntitiesInSight(mPasture.getPosition(entity), mSight);
    	if(list !=null)
    	{
    		for(Entity e : list)
    		{
    			double distance = mPasture.getPosition(entity).distance(mPasture.getPosition(e));
    			if(enemy(e))
    			{
    				if(closest == null || distance <= mPasture.getPosition(entity).distance(mPasture.getPosition(closest)))
    				{
    					closest = e;
    				}
    			}

    		}
    	}
    	return closest;
    }
    
	private boolean enemy(Entity enemy)
	{
		if(this instanceof Sheep && enemy instanceof Wolf)
			return true;
		else
			return false;
	}
	
	/**
	 * Returns a similar point from given list.
	 */
    public Point getSimilarPoint(List<Point> list)
    {
        Point current = mPasture.getPosition(this);
        Point lastPoint = new Point(current.x - mLastPos.x, current.y - mLastPos.y);
        List<Point> similiarPoints = new ArrayList<Point>();
        Point newPoint;
        for(Point p : list)
        {
            newPoint = new Point(p.x - current.x, p.y - current.y);
            if(mPasture.checkSimilarity(lastPoint, newPoint))
                similiarPoints.add(p);
            
        }
        return mPasture.getRandomMember(similiarPoints);
    }
	
	/**
	 * Returns point that was evaluated to be most suitable.
	 */
	public Point getEvaluatedPoint(List<Point> list, Entity enemy, Entity food)
	{
		Point point = null;
        double evaluationMax=-99;
        double evaluation;
        for(Point p : list)
        {
            evaluation = evaluate(p,enemy,food);
            if(evaluation > evaluationMax){
                point = p;
                evaluationMax = evaluation;            
            }
        }
        return point;
	}
	
	/**
	 * Evaluates and returns a value of a point with given entities.
	 */
	private double evaluate(Point point, Entity enemy, Entity food)
	{
		double e = -1;
		double f = 99;
        if(enemy!=null)
            e = point.distance(mPasture.getPosition(enemy));
        if(food!=null)
            f = point.distance(mPasture.getPosition(food));;
        return 100*e + 100/(f+1);
	}
	
	public void hunger() 
	{
		mStarveTimer--;
		if(mStarveTimer == 0)
			die();
	}
	
	public boolean hasEaten()
	{
		return mHasEaten;
	}
	
	public void breed()
	{
		if(mReproductionTimer <= 0)
		{
			Point safePosition = mPasture.getSafePosition(this);
			if(safePosition != null && hasEaten())
			{
				if(this instanceof Sheep)
				{
					Entity offspring = new Sheep(mPasture, mPasture.reproductionTimerSheep, mPasture.starveTimerSheep, mPasture.speedSheep, mPasture.sightRadiusSheep);
					mReproductionTimer = mPasture.reproductionTimerSheep;
					mPasture.addEntity(offspring, safePosition);
				}
				else if(this instanceof Wolf)
				{
					Entity offspring = new Wolf(mPasture, mPasture.reproductionTimerWolf, mPasture.starveTimerWolf, mPasture.speedWolf, mPasture.sightRadiusWolf);
					mReproductionTimer = mPasture.reproductionTimerWolf;
					mPasture.addEntity(offspring, safePosition);
				}
				mHasEaten = false;
			}
			
		}
		else
			mReproductionTimer--;
	}
	public abstract void setStarveTimer(int value);
	public abstract void setMoveDelay(int value);
}
