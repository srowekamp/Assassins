//
//  Player.swift
//  Assassins
//
//  Created by Scott Rowekamp on 10/3/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import Foundation
import SwiftyJSON

class Player {

    var id:Int
    var username:String
    var password:String
    var real_name:String
    var image_filename:String
    
    var games_played:Int
    var total_kills:Int
    
    var x_location:Double?
    var y_location:Double?
    
    init(data:JSON) {
        self.id = data["id"].int!
        self.username = data["username"].string!
        self.password = data["password"].string!
        self.real_name = data["real_name"].string!
        self.image_filename = data["image_filename"].string!
        self.games_played = data["games_played"].int!
        self.total_kills = data["total_kills"].int!
        self.x_location = data["x_location"].double
        self.y_location = data["y_location"].double
    }

    func printDebugInfo() {
        print("\nPlayer Information\n")
        print("ID: \(id)")
        print("Username: \(username)")
        print("Password: \(password)")
        print("Real Name: \(real_name)")
        print("Games Played: \(games_played)")
        print("Total Kills: \(total_kills)")
        print("X Location: \(x_location)")
        print("Y Location: \(y_location)")
        print("\nEnd of Player Information\n")
    }
}
