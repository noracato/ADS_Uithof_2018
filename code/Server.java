class Server{
	int id;
	private boolean idle = true;
	public Server(int id){
		this.id = id;
	}
	public void serveCustomer(double serveTime){
		this.idle = false;
	}
	public void makeAvailable(){
		this.idle = true;
	}
}