package com.ameron32.game.kingsburg.core.state;

import com.ameron32.game.kingsburg.core.state.ProvinceBoard;

public class MainProvinceBoardTest {

	static ProvinceBoard[] playerBoards = new ProvinceBoard[5];
	
	public static void main(String[] args) {
		for (int i = 0; i < 5; i++) { playerBoards[i] = new ProvinceBoard(); }
		ProvinceBoard a = playerBoards[0];
		a.buyNextBuilding(ProvinceBoard.ROW_RELIGION, false);
		a.buyNextBuilding(ProvinceBoard.ROW_RELIGION, false);
		a.buyNextBuilding(ProvinceBoard.ROW_RELIGION, false);
		a.buyNextBuilding(ProvinceBoard.ROW_RELIGION, false);
		System.out.println(a.hasBuilding("Statue"));
		System.out.println(a.hasBuilding("Chapel"));
		System.out.println(a.hasBuilding("Church"));
		System.out.println(a.hasBuilding("Cathedral"));
		a.buyNextBuilding(ProvinceBoard.ROW_MERCHANT, false);
		a.buyNextBuilding(ProvinceBoard.ROW_MERCHANT, false);
		a.buyNextBuilding(ProvinceBoard.ROW_MERCHANT, false);
		a.buyNextBuilding(ProvinceBoard.ROW_MERCHANT, false);
		System.out.println(a.hasBuilding("Inn"));
		System.out.println(a.hasBuilding("Market"));
		System.out.println(a.hasBuilding("Farms"));
		System.out.println(a.hasBuilding("Merchants' Guild"));
		System.out.println(a.loseBestBuilding());
		System.out.println(a.loseBestBuilding());
		System.out.println(a.loseBestBuilding());
		System.out.println(a.loseBestBuilding());
		System.out.println(a.loseBestBuilding());
	}
}
