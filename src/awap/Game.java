package awap;

import java.util.List;

import com.google.common.base.Optional;

public class Game {
	private State state;
	private Integer number;
	Point coin1 = new Point(9,2);
	Point coin2 = new Point(7,8);
	Point coin3 = new Point(13,8);
	Point coin4 = new Point(7,13);
	Point coin5 = new Point(13,13);
	Point coin6 = new Point(10,18);
	Point coin7 = new Point(3,10);
	Point coin8 = new Point(17,9);

	Point[] list = {coin1, coin2, coin3, coin4, coin5, coin6, coin7, coin8};

	public Optional<Move> updateState(State newState) {
		if (newState.getError().isPresent()) {
			Logger.log(newState.getError().get());
			return Optional.absent();
		}

		if (newState.getMove() != -1) {
			return Optional.fromNullable(findMove());
		}

		state = newState;
		if (newState.getNumber().isPresent()) {
			number = newState.getNumber().get();
		}

		return Optional.absent();
	}

	public int area(Block b) {
		System.out.println(b.getOffsets.size());
		return b.getOffsets.size();

	}

	public int scoreInc(Block b) {
		int score = area(b);
		System.out.println(score);
		List listOfOffSets = b.getOffsets();
		for (int i = 0; i < list.length; i++) {
			for (int j = 0; j < listOfOffSets.size(); j++) {
				if (listOfOffSets.get(j).equals(list[i])) {
					score += 2;
				} 
			}
		}
		System.out.println(score);
		return score;
	}
	public Point bestPlace(Block b) {
		List listOff = b.getOffsets();
		Point bestPoint = new Point(0,0);
		int lastDiff = 50;
		for(int a = 0; a < listOff.size(); a++) {
		if(bestPosCornerBarrier(b).getX() - listOff.get(a).getX() < lastDiff); {
			bestPoint = new Point(bestPosCornerBarrier(b).getX(), bestPosCornerBarrier(b).getY());
			lastDiff = bestPosCornerBarrier(b).getX() - listOff.get(a).getX();
		}
		}
		return bestPoint;
	}
	public int possibleArea(Block b) {
		List listOff = b.getOffsets();
		int posArea = 0;
		for(int i = 0; i<listOff.size(); i++) {
			if(canPlace(b, listOff.get(i))) {
				posArea++;
			}
		}
	}
	public Point bestPosCornerBarrier(Block b) {
		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 20; j++) {
				if((canPlace(b, new Point(i, j)) && !canPlace(b, new Point(i, j+1)) && !canPlace(b, new Point(i+1, j)))) {
					return new Point(i,j);
				}
			}
		}
	}
	private Move findMove() {
		int N = state.getDimension();
		List<Block> blocks = state.getBlocks().get(number);
		int highestScoreAdded = 0;
		int xCoordinate = 0;
		int yCoordinate = 0;
		int rotation = 0;
		int block = 0;
		
		for (int x = 0; x < N; x++) {  //x-pos
			for (int y = 0; y < N; y++) { //y-pos
				for (int rot = 0; rot < 4; rot++) { //loops through the rotation for the block
					for (int i = 0; i < blocks.size(); i++) { //loops through all blocks
						if (canPlace(blocks.get(i).rotate(rot), new Point(x, y))) { //checking for valid position
							Point bitch = bestPlace(blocks.get(i));
							if (scoreInc(blocks.get(i)) > highestScoreAdded) {
								highestScoreAdded = scoreInc(blocks.get(i));
								block = i;
								rotation = rot;
								xCoordinate = bitch.getX();
								yCoordinate = bitch.getY();
							}
							
						}
					}
				}
			}
		}

		return new Move(block, rotation, xCoordinate, yCoordinate);
	}
	
	private int getPos(int x, int y) {
		return state.getBoard().get(x).get(y);
	}

	private boolean canPlace(Block block, Point p) {
		boolean onAbsCorner = false, onRelCorner = false;
		int N = state.getDimension() - 1;

		Point[] corners = { new Point(0, 0), new Point(N, 0), new Point(N, N),
				new Point(0, N) };
		;
		Point corner = corners[number];

		for (Point offset : block.getOffsets()) {
			Point q = offset.add(p);
			int x = q.getX(), y = q.getY();

			if (x > N || x < 0 || y < 0 || y > N
          || getPos(x, y) >= 0
          || getPos(x, y) == -2
					|| (x > 0 && getPos(x - 1, y) == number)
					|| (y > 0 && getPos(x, y - 1) == number)
					|| (x < N && getPos(x + 1, y) == number)
					|| (y < N && getPos(x, y + 1) == number)) {
				return false;
			}

			onAbsCorner = onAbsCorner || q.equals(corner);
			onRelCorner = onRelCorner
					|| (x > 0 && y > 0 && getPos(x - 1, y - 1) == number)
					|| (x < N && y > 0 && getPos(x + 1, y - 1) == number)
					|| (x > 0 && y < N && getPos(x - 1, y + 1) == number)
					|| (x < N && y < N && getPos(x + 1, y + 1) == number);
		}

		return !((getPos(corner.getX(), corner.getY()) < 0 && !onAbsCorner) || (!onAbsCorner && !onRelCorner));
	}
}
