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

protocol updateable {
    func updateUI()
}

struct GameTime {
    var hour:Int = 0
    var min:Int = 0
    var sec:Int = 0
    var isValid = false
    
    init () {
    
    }
    
    init (hour:Int, min:Int, sec:Int) {
        self.hour = hour
        self.min = min
        self.sec = sec
        isValid = true
    }
    
    func timeString(time:Int) -> String {
        var intToString = String(time)
        switch intToString.characters.count {
        case 1:
            return "0\(intToString)"
        case 2:
            return intToString
        default:
            return "00"
        }
    }
    
    func timeString() -> String {
        return "\(timeString(time: hour))\(timeString(time: min))\(timeString(time: sec))"
    }
}

class Game {
    
    var updateID:Double = 0.0
    
    let id:Int
    let gameID:String
    let password:String
    let xcenter:Double
    let ycenter:Double
    let radius:Int
    let hostID:Int
    let duration:Int
    
    var end_time:GameTime
    var players_list:[Int]?
    var player_object_list:[Player] = [Player]()
    var players_alive:[Int]?
    
    var isRunning = false
    var viewsToUpdate = [updateable]()
    
    
    init(data:JSON){
        self.id = data["id"].int!
        self.gameID = data["gameid"].string!
        self.password = data["password"].string!
        self.xcenter = data["xcenter"].double!
        self.ycenter = data["ycenter"].double!
        self.radius = data["radius"].int!
        self.hostID = data["hostid"].int!
        self.duration = data["duration"].int!
        self.end_time = GameTime()
        
        if let timeString = data["end_time"].string {
            self.end_time = stringToGameTime(time: timeString)
        }
        
        self.players_list = data["players_list"].arrayObject as? [Int]
        self.players_alive = data["players_alive"].arrayObject as? [Int]
    }
    
    func updateInfo(data: JSON) {
        if let timeString = data["end_time"].string {
            self.end_time = stringToGameTime(time: timeString)
        } else {
            end_time = GameTime()
        }
        self.players_list = data["players_list"].arrayObject as? [Int]
        self.players_alive = data["players_alive"].arrayObject as? [Int]
        
        if end_time.isValid {
            isRunning = true
        } else {
            isRunning = false
        }
        
        // update other views
        for vc in viewsToUpdate {
            vc.updateUI()
        }
    }
    
    func printDebugInfo() {
        print("\nGame Information \(updateID) \n")
        updateID += 1
        print("Host ID: \(id)")
        print("Game ID: \(gameID)")
        print("Password: \(password)")
        print("X Center: \(xcenter)")
        print("Y Center: \(ycenter)")
        print("Radius: \(radius)")
        print("Game Duration: \(duration)")
        print("End Time: \(end_time.timeString())")
        print("\nEnd of Game Infromation\n")
    }
    
    func printPlayerList() {
        print("\nPlayer List for \(id)\n")
        for item in player_object_list {
            print("ID: \(item.id) --- NAME: \(item.real_name)")
        }
        print("\nEnd of PlayerList\n")
    }
    
    func stringToGameTime(time: String) -> GameTime {
        if time.characters.count != 6 {
            print("WARNING: Unacceptable Time String")
            return GameTime()
        } else {
            var timeString = [Character](time.characters)
            let hours = Int("\(timeString[0])\(timeString[1])")!
            let mins = Int("\(timeString[2])\(timeString[3])")!
            let seconds = Int("\(timeString[4])\(timeString[5])")!
            return GameTime(hour: hours, min: mins, sec: seconds)
        }
    }
}
