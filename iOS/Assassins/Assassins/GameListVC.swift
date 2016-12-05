//
//  GameListVC.swift
//  Assassins
//
//  Created by Scott Rowekamp on 12/4/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import UIKit
import Alamofire
import SwiftyJSON

class GameListVC: UIViewController, UITableViewDelegate, UITableViewDataSource {

    @IBOutlet weak var tableView: UITableView!
    
    let GET_GAMES_URL = "http://proj-309-la-05.cs.iastate.edu/active_games.php"
    
    var currentGames = [String]()
    var hasUpdated = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.delegate = self
        tableView.dataSource = self
        downloadGameData()
    }
    
    func downloadGameData() {
        Alamofire.request(GET_GAMES_URL).responseJSON { response in
            if response.result.isSuccess {
                let returned_data = JSON(response.result.value!)
                //print(returned_data)
                for game_data in returned_data.arrayValue {
                    //print(game_data)
                    if let gamename = game_data["gameid"].string {
                        self.currentGames.append(gamename)
                    }
                }
                self.hasUpdated = true
                self.tableView.reloadData()
            } else {
                self.popUpAlert(title: "Server Call Failed", message: "Please check your network connection and verify you are using the IOWA STATE VPN", handler: nil)
            }
        }
    }
    
    func numberOfSections(in tableView: UITableView) -> Int {
        return 1;
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        if hasUpdated {
            return currentGames.count
        } else {
            return 1;
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if hasUpdated {
            let cell = tableView.dequeueReusableCell(withIdentifier: "gameCell")!
            cell.textLabel?.text = currentGames[indexPath.row]
            return cell
        } else {
            let cell = tableView.dequeueReusableCell(withIdentifier: "gameCell")!
            cell.textLabel?.text = "Not Updated Yet"
            return cell
        }
    }
}
