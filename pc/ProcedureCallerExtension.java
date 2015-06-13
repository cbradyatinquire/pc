package pc;

import java.util.Map;

import javax.swing.SwingUtilities;

import org.nlogo.agent.Observer;
import org.nlogo.api.Argument;
import org.nlogo.api.CompilerException;
import org.nlogo.api.Context;
import org.nlogo.api.DefaultClassManager;
import org.nlogo.api.DefaultCommand;
import org.nlogo.api.DefaultReporter;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.LogoListBuilder;
import org.nlogo.api.PrimitiveManager;
import org.nlogo.api.SimpleJobOwner;
import org.nlogo.api.Syntax;
import org.nlogo.app.App;
import org.nlogo.nvm.ExtensionContext;
import org.nlogo.nvm.Procedure;
import org.nlogo.nvm.Workspace;
import org.nlogo.workspace.AbstractWorkspace;

public class ProcedureCallerExtension extends DefaultClassManager {

	@Override
	public void load(PrimitiveManager pm) throws ExtensionException {
		//pm.addPrimitive( "run-all", new RunAll() );
		pm.addPrimitive( "list-all", new ListAll() );
	}

	

	public static class RunAll extends DefaultCommand {

		@Override
		public String getAgentClassString() {
			return "O";
		}
		
		@Override
	    public Syntax getSyntax() {
	       int[] argTypes = {Syntax.StringType() };
	       return  Syntax.commandSyntax(argTypes);
	    }
		
		@Override
		public void perform(Argument[] args, Context context)
				throws ExtensionException, LogoException {
			String prefix = args[0].getString().toUpperCase();
			
			final ExtensionContext ec = ((ExtensionContext)context);
			Workspace ws = ec.workspace();
			Map<String,Procedure> pMap = ws.getProcedures();
			for ( String procedureName :  pMap.keySet() ) {
				if ( procedureName.startsWith(prefix) ) {
					Procedure p = pMap.get(procedureName);
					if (p.usableBy.contains("O") && p.args.isEmpty() && p.tyype.toString().equalsIgnoreCase("command") ) {
						try {
							App.app().commandLater(procedureName);
						} catch (CompilerException e) {
							e.printStackTrace();
							throw new ExtensionException(e.getMessage());
						}
					}
				}
			}
		}
		
	}

	
	public static class ListAll extends DefaultReporter {

		
		@Override
		public Syntax getSyntax() {
			int[] argType = {Syntax.StringType()};
			int retType = Syntax.ListType();
			return  Syntax.reporterSyntax( argType, retType );
		}
		
		@Override
		public Object report(Argument[] args, Context context)
				throws ExtensionException, LogoException {
			
			String prefix = args[0].getString().toUpperCase();
			
			
			ExtensionContext ec = ((ExtensionContext)context);
			Workspace ws = ec.workspace();
			Map<String,Procedure> pMap = ws.getProcedures();			
			LogoListBuilder llb = new LogoListBuilder();
						
			for ( String procedureName :  pMap.keySet() ) {
				if ( procedureName.startsWith(prefix) ) {
					Procedure p = pMap.get(procedureName);
					if (p.usableBy.contains("O") && p.args.isEmpty() && p.tyype.toString().equalsIgnoreCase("command") ) {
						llb.add(procedureName);
					}
				}
			}
			return llb.toLogoList();
		}

		
		
	}
}
