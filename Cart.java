import java.util.ArrayList;

//Name: Vincent Bercze
//Student ID: 501105440


public class Cart
{
	ArrayList<CartItem> cartItems = new ArrayList<CartItem>();
	String productId = "";
	String customerID = "";
	
	public Cart(String customerID)
	{
		this.customerID = customerID;
	}
	
	public void addprods(CartItem item)
	{
		cartItems.add(item);
	}
	
	public ArrayList<CartItem> getCartItems()
	{
		return cartItems;
	}
	
	public void removeprods(CartItem item)
	{
		cartItems.remove(item);
	}
	
}
