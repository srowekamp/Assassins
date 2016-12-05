//
//  Game.swift
//  Assassins
//
//  Created by Scott Rowekamp on 11/6/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import Foundation
import Alamofire
import SwiftyJSON

class Game {
    
    let id:Int
    let gameID:String
    let password:String
    let xcenter:Double
    let ycenter:Double
    let radius:Int
    let hostID:Int
    let duration:Int
    
    var end_time:Int?
    var players_list:[Int]?
    var player_object_list:[Player] = [Player]()
    var players_alive:[Int]?
    
    var isRunning = false
    
    
    
    init(data:JSON){
        self.id = data["id"].int!
        self.gameID = data["gameid"].string!
        self.password = data["password"].string!
        self.xcenter = data["xcenter"].double!
        self.ycenter = data["ycenter"].double!
        self.radius = data["radius"].int!
        self.hostID = data["hostid"].int!
        self.duration = data["duration"].int!
        self.end_time = data["end_time"].int
        self.players_list = data["players_list"].arrayObject as? [Int]
        self.players_alive = data["players_alive"].arrayObject as? [Int]
    }
    
    func updateInfo(data: JSON) {
        self.end_time = data["end_time"].int
        self.players_list = data["players_list"].arrayObject as? [Int]
        self.players_alive = data["players_alive"].arrayObject as? [Int]
        
        // RRROROOROROROROROROORORORORORO with end_time
        
        print(end_time ?? "no end time")
        
        if end_time == nil {
            isRunning = false
        } else {
            isRunning = true
        }
        
    }
    
    func printDebugInfo() {
        print("\nGame Information\n")
        print("Host ID: \(id)")
        print("Game ID: \(gameID)")
        print("Password: \(password)")
        print("X Center: \(xcenter)")
        print("Y Center: \(ycenter)")
        print("Radius: \(radius)")
        print("Game Duration: \(duration)")
        print("\nEnd of Game Infromation\n")
    }
    
    func printPlayerList() {
        print("\nPlayer List for \(id)\n")
        for item in player_object_list {
            print("ID: \(item.id) --- NAME: \(item.real_name)")
        }
        print("\nEnd of PlayerList\n")
    }
    
    func updatePlayerList(xcord:Double, ycord:Double ) {
        
    }
    
    
    
    
}
