//
//  Game.swift
//  Assassins
//
//  Created by Scott Rowekamp on 11/6/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import Foundation
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
    var players_alive:[Int]?
    
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
}
