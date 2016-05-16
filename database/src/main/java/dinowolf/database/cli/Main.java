package dinowolf.database.cli;

public class Main {

	public static final void main(String[] args) {
		System.out.println("#1: dinowolf::database");
		Cli cli = new Cli(args);
		cli.parse();
		
	}
}
