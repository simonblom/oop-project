package pasture;
import javax.swing.ImageIcon;

public abstract class Idle implements Entity
{
	private Pasture mPasture;
	private ImageIcon mImageIcon;
	
	public Idle(Pasture pasture, String imagePath)
	{
		mPasture = pasture;
		mImageIcon = new ImageIcon(imagePath);
	}
	
	@Override
	public ImageIcon getImage() 
	{
		return mImageIcon;
	}
	
	public Pasture getPasture()
	{
		return mPasture;
	}
	
	public boolean alive()
	{
		if(mPasture.getEntities().contains(this))
			return true;
		else
			return false;
	}
}
