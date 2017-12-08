package ufl.cs1.controllers;

import game.controllers.DefenderController;
import game.controllers.benchmark.Devastator;
//import game.controllers.example.StudentController;
import game.models.Attacker;
import game.models.Defender;
import game.models.Game;
import game.models.Actor;
import game.models.*;
import game.system._Node;


//top down, left to right


import java.util.ArrayList;
import java.util.List;


public final class StudentController implements DefenderController {
	private Game currentGame = null;
	private Attacker devastator = null;
	private List<Defender> defendersList = null;
	private Defender redHunter;
	private Defender pinkChaser;
	private Defender orangePursuer;
	private Defender blueGoalie;

	private attackingDefender redHunterClass;
	private attackingDefender pinkChaserClass;
	private attackingDefender orangePursuerClass;
	private blockingDefender blueGoalieClass;

	private helperClass helpers;

	public void init(Game game)
	{
	}

	public int[] update(Game game, long timeDue) {
		int[] actions = new int[4];
		StudentController.this.currentGame = game;
		StudentController.this.devastator = game.getAttacker();
		StudentController.this.defendersList = currentGame.getDefenders();

		redHunter = defendersList.get(0);
		pinkChaser = defendersList.get(1);
		orangePursuer = defendersList.get(2);
		blueGoalie = defendersList.get(3);

		redHunterClass = new AttackDefender1(redHunter);
		pinkChaserClass =  new AttackDefender1(pinkChaser);
		orangePursuerClass  = new AttackDefender1(orangePursuer);
		blueGoalieClass  = new BlockingDefender1(blueGoalie);

		helpers = new helperClass(game,devastator,redHunter,pinkChaser,orangePursuer,blueGoalie);

		/*
			actions[0] = redHunterClass.flee(this.currentGame.getAttacker().getLocation()); //red
			actions[1] = pinkChaserClass.flee(this.currentGame.getAttacker().getLocation()); //pinky
			actions[2] = orangePursuerClass.flee(this.currentGame.getAttacker().getLocation()); //orange
		//this is temporary until Hannah creates the class definition
			actions[3] = redHunterClass.chaseObject(this.currentGame.getAttacker().getLocation()); //teal/blue = Inky
		*/
		actions[0] = redHunterClass.flock(redHunter, currentGame.getPowerPillList().get(0));
		actions[1] = pinkChaserClass.flock(pinkChaser, currentGame.getPowerPillList().get(0));
		actions[2] = orangePursuerClass.flock(orangePursuer,currentGame.getPowerPillList().get(0));
		List<Node> pills = currentGame.getPowerPillList();
		if (pills.size() > 0)
		actions[3] = redHunterClass.chaseObject(pills.get(pills.size() - 1));
		else
			actions[3] = pinkChaserClass.chaseObject(devastator.getLocation());

		return actions;
	}

	public class AttackDefender1 implements attackingDefender{
		private Defender thisDefender;
		AttackDefender1(Defender _thisDefender)
		{
			thisDefender = _thisDefender;
		}
		public int uodateDefender()
		{
			if (thisDefender.getLocation().isJunction());
				return 0;
			else
			{

			}


			return 0;
		}

		public int chaseObject(Node target)
		{
			int action = 0;
			if (target != null)
			{
				//action = thisDefender.getNextDir(target, true);
				try {List<Node> paths = thisDefender.getPathTo(target);
				action = thisDefender.getNextDir(paths.get(1), true);}
				catch (Exception e)
				{
					action = thisDefender.getNextDir(devastator.getLocation(), true);
				}
			}
			else
			{
				action = thisDefender.getNextDir(devastator.getLocation(), true);
			}

			return action;
		}
		public int shadowObject(Actor actor)
		{
			int actions = 0;
			int actorDirection = actor.getDirection();
			return actorDirection;
		}
		public int flee(Node target)
		{
			int action = 0;
			if (target != null)
			{
				action = thisDefender.getNextDir(target, false);
			}
			else
			{
				action = thisDefender.getNextDir(devastator.getLocation(), false);
			}
			return action;
		}
		public int distract(Defender distractor)
		{
			return distractor.getNextDir(helpers.getNearestEmptyNode(distractor), true);
		}
		public int sacrifice(Defender martyr)
		{
			return martyr.getNextDir(devastator.getLocation(), true);
		}
		public int defend(Defender defender)
		{
			List<Node> powerPillList = currentGame.getPowerPillList();
			if (powerPillList != null) //if there are no power pills, attack or flee depending on lair Time
			{
				if (defender.isVulnerable())
					return defender.getNextDir(devastator.getLocation(), false);
				else
					return defender.getNextDir(devastator.getLocation(), true);
			}
			else
			{
				Node pillClosestToDevastator = devastator.getTargetNode(powerPillList, true);
				powerPillList.remove(pillClosestToDevastator);
				Node secondClosestPill = devastator.getTargetNode(powerPillList, true);
				if (devastator.getLocation().getPathDistance(pillClosestToDevastator) < 10)
					return defender.getNextDir(secondClosestPill, true);
				else
					return defender.getNextDir(pillClosestToDevastator, true);
			}
		}
		public int flock(Defender defender)
		{
			Actor closestDefender = devastator.getTargetActor(defendersList, true); // determines the closest defender
			List<Node> pathTo = defender.getPathTo(closestDefender.getLocation());
			if(pathTo.size() > 1)
				return defender.getNextDir(pathTo.get(1), true);
			else
				return defender.getNextDir(devastator.getLocation(), true);
		}
		public int flock(Defender defender, Node target)
		{
			List<Node> pathTo = defender.getPathTo(target);
			if(pathTo.size() > 1)
				return defender.getNextDir(pathTo.get(1), true);
			else
				return defender.getNextDir(devastator.getLocation(), true);
		}
	}


	public class BlockingDefender1 implements blockingDefender
	{
		Defender thisDefender;

		BlockingDefender1(Defender _thisDefender)
		{
			thisDefender = _thisDefender;
		}
		//based on the current game conditions it will decide what the defender should do
		public void determineMode()
		{

		}
		//defender will pace around the node to defend it
		public int paceNodeMode(Node target)
		{
			return 0;
		}
		//simply runs away from devastator
		public int fleeMode()
		{
			return 0;
		}
		//depending on devastator location and the location of the other ghosts, it will draw devastator towards empty nodes
		public int distractMode()
		{
			return 0;
		}
	}
	public enum defenderMode {
		chaseMode,
		fleeMode,
		distractMode,
		defendMode,
		flockMode;

		private defenderMode() {}
	}
	public enum defenderStatus {
		normal,
		vulnerable,
		blinking;
	}
	public void shutdown(Game game)
	{
	}

	public static class helperClass implements helperMethods {
		private Game currentGame;
		private Attacker devastator;
		private Defender redHunter;
		private Defender pinkChaser;
		private Defender orangePursuer;
		private Defender blueGoalie;

		helperClass(Game _currentGame, Attacker _devastator, Defender _redHunter, Defender _pinkChaser, Defender _orangePursuer, Defender _blueGoalie)
		{
			currentGame = _currentGame;
			devastator = _devastator;
			redHunter = _redHunter;
			pinkChaser = _pinkChaser;
			orangePursuer = _orangePursuer;
			blueGoalie = _blueGoalie;
		}
		public Node closestPowerPillToDevastator() {
			List<Node> powerPillList = currentGame.getPowerPillList();
			if (powerPillList.size() > 0)
				return devastator.getTargetNode(powerPillList, true);
			else
				return null;
		}

		public int devastatorToPillDistance()
		{
			Node closestPowerPill = closestPowerPillToDevastator();
			if (closestPowerPill == null)
				return -1;
			else return devastator.getLocation().getPathDistance(closestPowerPill);
		}
		public defenderStatus vulnerableStatus(Defender defender)
		{
			int lairTime = defender.getVulnerableTime();
			if (lairTime == 0)
				return defenderStatus.normal;
			else if (lairTime > 50)
				return defenderStatus.vulnerable;
			else
				return defenderStatus.blinking;
		}

		public Node getNearestEmptyNode(Actor personOfInterest)
		{
			Node to = personOfInterest.getLocation();
			List<Node> neighbors = to.getNeighbors();
			if (neighbors.size() > 0)
			{
				for (Node node : neighbors)
				{
					if (node.isPill()) return node;
				}
			}
			List<Defender> defenderList = currentGame.getDefenders();
			int direction = 0;
			direction = personOfInterest.getNextDir(personOfInterest.getTargetActor(defenderList, true).getLocation(), false); //finds the closest actor and moces away from him
			return to.getNeighbor(direction);
		}

	}


		public interface helperMethods
		{
		Node closestPowerPillToDevastator();
		int devastatorToPillDistance();
		defenderStatus vulnerableStatus(Defender defender);
		Node getNearestEmptyNode(Actor personOfInterest);
		}
	public interface attackingDefender
	{
		int chaseObject(Node target);
		int shadowObject(Actor actor);
		int flee(Node target);
		int distract(Defender distractor);
		int sacrifice(Defender martyr);
		int defend(Defender defender);
		int flock(Defender defender, Node target);
		int flock(Defender defender);
	}
	public interface blockingDefender{
		//based on the current game conditions it will decide what the defender should do
		void determineMode();
		//defender will pace around the node to defend it
		int paceNodeMode(Node target);
		//simply runs away from devastator
		int fleeMode();
		//depending on devastator location and the location of the other ghosts, it will draw devastator towards empty nodes
		int distractMode();

	}
}
