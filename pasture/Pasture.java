package pasture;
import java.util.*;
import java.awt.Point;


/**
 * A pasture contains sheep, wolves, fences, plants, and possibly
 * other entities. These entities move around in the pasture and try
 * to find food, other entities of the same kind and run away from
 * possible enimies. 
 */
public class Pasture 
{
    private final int   width = 35;
    private final int   height = 24;
    
    /* Default values for variables. */
    private final int   wolves = 10;
    private final int   sheep = 20;
    private final int   grass = 40;
    private final int   fence = 40;
    protected int starveTimerWolf = 200;
    protected int starveTimerSheep = 100;
    protected int reproductionTimerWolf = 201;
    protected int reproductionTimerSheep = 101;
    protected int reproductionTimerGrass = 20;
    protected int speedWolf = 16;
    protected int speedSheep = 10;
    protected int sightRadiusWolf = 4;
    protected int sightRadiusSheep = 3;
    /**/
    
    private final Set<Entity> world = new HashSet<Entity>();
    private final Map<Point, List<Entity>> grid = new HashMap<Point, List<Entity>>();
    private final Map<Entity, Point> point = new HashMap<Entity, Point>();

    private final PastureGUI gui;
    private final Scanner input;

    /** 
     * Creates a new instance of this class and places the entities in
     * it on random positions.
     */
    public Pasture() {

        Engine engine = new Engine(this);
        input = new Scanner(System.in);
        init(input);
        gui = new PastureGUI(width, height, engine);

        /* The pasture is surrounded by a fence. */
        for (int i = 0; i < width; i++) {
            addEntity(new Fence(this), new Point(i,0));
            addEntity(new Fence(this), new Point(i, height - 1));
        }
        for (int i = 1; i < height-1; i++) {
            addEntity(new Fence(this), new Point(0,i));
            addEntity(new Fence(this), new Point(width - 1,i));
        }

        /* 
         * Now insert the right number of different entities in the
         * pasture.
         */
        for (int i = 0; i < fence; i++) {
        	Entity fence = new Fence(this);
            addEntity(fence, getFreePosition(fence));
        }
        for (int i = 0; i < wolves; i++) {
            Entity wolf = new Wolf(this, reproductionTimerWolf, starveTimerWolf, speedWolf, sightRadiusWolf);
            addEntity(wolf, getFreePosition(wolf));
        }
        for (int i = 0; i < sheep; i++) {
            Entity sheep = new Sheep(this, reproductionTimerSheep, starveTimerSheep, speedSheep, sightRadiusSheep);
            addEntity(sheep, getFreePosition(sheep));
        }
        for (int i = 0; i < grass; i++) {
            Entity grass = new Grass(this, reproductionTimerGrass);
            addEntity(grass, getFreePosition(grass));
        }
        gui.update();
    }
    
    public void init(Scanner input)
    {
    	System.out.println("Do you wish to change settings for entities? (Y/N)");
    	String answer = input.next();
    	if(answer.equalsIgnoreCase("y"))
    	{
    		System.out.println("Starvation Timer of entities (100 - 500): ");
    		System.out.print("Wolf: ");
    		starveTimerWolf = input.nextInt();
    		if(starveTimerWolf < 100 || starveTimerWolf > 500) 
    		{
    			System.out.println("Invalid input, setting default: 200");
    			starveTimerWolf = 200;
    		}
    		System.out.print("Sheep: ");
    		starveTimerSheep = input.nextInt();
    		if(starveTimerSheep < 100 || starveTimerSheep > 500) 
    		{
    			System.out.println("Invalid input, setting default: 100");
    			starveTimerSheep = 100;
    		}
    		System.out.println("Reproduction Timer of entities (20 - 501): ");
    		System.out.print("Wolf: ");
    		reproductionTimerWolf = input.nextInt();
    		if(reproductionTimerWolf < 20 || reproductionTimerWolf > 501) 
    		{
    			System.out.println("Invalid input, setting default: 201");
    			reproductionTimerWolf = 201;
    		}
    		System.out.print("Sheep: ");
    		reproductionTimerSheep = input.nextInt();
    		if(reproductionTimerSheep < 20 || reproductionTimerSheep > 501) 
    		{
    			System.out.println("Invalid input, setting default: 101");
    			reproductionTimerSheep = 101;
    		}
    		System.out.print("Grass: ");
    		reproductionTimerGrass = input.nextInt();
    		if(reproductionTimerGrass < 20 || reproductionTimerGrass > 501) 
    		{
    			System.out.println("Invalid input, setting default: 20");
    			reproductionTimerGrass = 20;
    		}
    		System.out.println("Speed of entities: (1 - 20)");
    		System.out.print("Wolf: ");
    		speedWolf = input.nextInt(); 
    		if(speedWolf < 1 || speedWolf > 20) 
    		{
    			System.out.println("Invalid input, setting default: 16");
    			speedWolf = 16;
    		}
    		System.out.print("Sheep: ");
    		speedSheep = input.nextInt();
    		if(speedSheep < 1 || speedSheep > 20) 
    		{
    			System.out.println("Invalid input, setting default: 10");
    			speedSheep = 10;
    		}
    		System.out.println("Vision of entities(1 - 10): ");
    		System.out.print("Wolf: ");
    		sightRadiusWolf = input.nextInt(); 
    		if(sightRadiusWolf < 1 || sightRadiusWolf > 10) 
    		{
    			System.out.println("Invalid input, setting default: 4");
    			sightRadiusWolf = 4;
    		}
    		System.out.print("Sheep: ");
    		sightRadiusSheep = input.nextInt();
    		if(sightRadiusSheep < 1 || sightRadiusSheep > 10) 
    		{
    			System.out.println("Invalid input, setting default: 3");
    			sightRadiusSheep = 3;
    		}
    		System.out.println("Starting with given values");
    	}
    	else
    		System.out.println("Starting with default values");

    }

    public void refresh() {
        gui.update();
    }

    /**
     * Returns a random free position in the pasture if there exists
     * one.
     * 
     * If the first random position turns out to be occupied, the rest
     * of the board is searched to find a free position. 
     */
    private Point getFreePosition(Entity toPlace) 
        throws MissingResourceException {
        Point position = new Point((int)(Math.random() * width),
                                   (int)(Math.random() * height)); 

        int p = position.x+position.y*width;
        int m = height * width;
        int q = 97; //any large prime will do

        for (int i = 0; i<m; i++) {
            int j = (p+i*q) % m;
            int x = j % width;
            int y = j / width;

            position = new Point(x,y);
            boolean free = true;

            Collection <Entity> c = getEntitiesAt(position);
            if (c != null) {
                for (Entity thisThing : c) {
                    if(!toPlace.isCompatible(thisThing)) { 
                        free = false; break; 
                    }
                }
            }
            if (free) return position;
        }
        throw new MissingResourceException(
                  "There is no free space"+" left in the pasture",
                  "Pasture", "");
    }
    
    /**
     * Returns position of entity.
     */
    public Point getPosition (Entity e) {
        return point.get(e);
    }

    /**
     * Add a new entity to the pasture.
     */
    public void addEntity(Entity entity, Point pos) {

        world.add(entity);

        List<Entity> l = grid.get(pos);
        if (l == null) {
            l = new  ArrayList<Entity>();
            grid.put(pos, l);
        }
        l.add(entity);

        point.put(entity,pos);

        gui.addEntity(entity, pos);
    }
    
    /**
     * Move entity to a new position.
     */
    public void moveEntity(Entity e, Point newPos) {
        
        Point oldPos = point.get(e);
        List<Entity> l = grid.get(oldPos);
        if (!l.remove(e)) 
            throw new IllegalStateException("Inconsistent stat in Pasture");
        /* We expect the entity to be at its old position, before we
           move, right? */
                
        l = grid.get(newPos);
        if (l == null) {
            l = new ArrayList<Entity>();
            grid.put(newPos, l);
        }
        l.add(e);

        point.put(e, newPos);

        gui.moveEntity(e, oldPos, newPos);
    }

    /**
     * Remove the specified entity from this pasture.
     */
    public void removeEntity(Entity entity) { 

        Point p = point.get(entity);
        world.remove(entity); 
        grid.get(p).remove(entity);
        point.remove(entity);
        gui.removeEntity(entity, p);

    }

    /**
     * Various methods for getting information about the pasture
     */
    
    public List<Entity> getEntities() {
        return new ArrayList<Entity>(world);
    }
        
    public Collection<Entity> getEntitiesAt(Point lookAt) 
    {
        Collection<Entity> l = grid.get(lookAt);
        if (l==null) 
        {
            return null;
        }
        else 
        {
            return new ArrayList<Entity>(l);
        }
    }

    public List<Point> getNeighbors(Entity entity)
    {
        List<Point> neighbors = new ArrayList<Point>();
        int entityX = getPosition(entity).x;
        int entityY = getPosition(entity).y;
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                Point p = new Point(entityX + x, entityY + y);
                neighbors.add(p);
            }
        }
        return neighbors;
    }

    public List<Point> getFreeNeighbors(Entity entity) 
    {
        List<Point> free = new ArrayList<Point>();
        List<Point> neighbours = getNeighbors(entity);
        for(Point p : neighbours)
        {
        	if (freeSpace(p, entity))
        		free.add(p);
        }
        return free;
    }
    
    private boolean freeSpace(Point p, Entity e) {
                              
        List <Entity> l = grid.get(p);
        if ( l == null  ) return true;
        for (Entity old : l ) 
            if (! old.isCompatible(e)) return false;
        return true;
    }
    
    /**/
    
    /**
     * Returns a safe position if there is any.
     * @param entity
     * @return null if no point is safe
     */
    public Point getSafePosition(Entity entity)
    {
    	List<Point> neighbors = getNeighbors(entity);
    	for(Point p : neighbors)
    	{
    		Collection<Entity> entities = getEntitiesAt(p);
    		if(entities == null)
    		{
    			return p;
    		}
    		else
    		{
    			boolean safe = true;
    			for(Entity otherEntity : entities)
    			{
    				if(!entity.isCompatible(otherEntity))
    					safe = false;
    			}
    			if(safe)
    				return p;
    		}
    	}
    	return null;
    }
    
    /**
     * Returns entities in given sight radius.
     */
    public List<Entity> getEntitiesInSight(Point origin, int sight)
    {
        List<Entity> entities = new ArrayList<Entity>();
        for(Entity e : world) 
        {
            if( origin.distance(getPosition(e)) <= sight) 
            {
                entities.add( e );
            }
        }
        return entities;
    }
    
    /**
     * Returns true if points do not differ that much.
     */
    public boolean checkSimilarity(Point p1, Point p2)
    {
		if(p1.x == 0)
			return p1.y == p2.y;
		else if(p1.y == 0)
			return p1.x == p2.x;
		return Math.abs(p1.x-p2.x)<=1 && Math.abs(p1.y-p2.y)<=1;
    }
    
    /**
     * A general method for grabbing a random element from a list.
     */
    public <X> X getRandomMember(List<X> c) 
    {
        if (c.size() == 0)
            return null;
        
        int n = (int)(Math.random() * c.size());
        
        return c.get(n);
    }

    /** The method for the JVM to run. */
    public static void main(String[] args) {

        new Pasture();
    }
}