//
//  LobbyVC.swift
//  Assassins
//
//  Created by Scott Rowekamp on 10/4/16.
//  Copyright © 2016 LA-05. All rights reserved.
//

import UIKit
import MapKit
import CoreLocation
import Alamofire
import SwiftyJSON

var currentTarget:Player?
var gameUpdater:UIViewController?

class LobbyVC: UIViewController, MKMapViewDelegate, CLLocationManagerDelegate {
    
    let PLAYER_LIST_URL = "http://proj-309-la-05.cs.iastate.edu:8080/Assassins/GetPlayers"
    let UPDATE_GAME_URL = "http://proj-309-la-05.cs.iastate.edu:8080/Assassins/UpdateGame"
    let START_GAME_URL = "http://proj-309-la-05.cs.iastate.edu:8080/Assassins/GameStart"
    let STOP_GAME_URL = "http://proj-309-la-05.cs.iastate.edu:8080/Assassins/EndGame"
    
    var pressedStart = false
    var updateTimer:Timer!
    var sendRequests = true
    
    let locationManager = CLLocationManager()
    
    var center:CLLocationCoordinate2D?
    
    private var _playArea: MKCircle = MKCircle()
    var playArea:MKCircle {
        get {
            return _playArea
        }
        set {
            mapView.remove(_playArea)
            _playArea = newValue
            mapView.add(newValue)
        }
    }

    @IBOutlet weak var mapView: MKMapView!
    
    @IBAction func goBack(_ sender: AnyObject) {
        self.dismiss(animated: true, completion: nil)
    }
    
    @IBOutlet weak var startStopButton: UIBarButtonItem!
    
    @IBAction func pressedStartStopButton(_ sender: Any) {
        if pressedStart == false {
            // call game start
            
            var paramaters = [String:String]()
            paramaters["gameid"] = "\(game!.gameID)"
            
            // setup current time
            let date = Date()
            let calendar = Calendar.current
            
            var hour = String(calendar.component(.hour, from: date))
            var minutes = String(calendar.component(.minute, from: date))
            var seconds = String(calendar.component(.second, from: date))
            
            if hour.characters.count < 2 {
                hour = "0\(calendar.component(.hour, from: date))"
            }
            
            if minutes.characters.count < 2 {
                minutes = "0\(calendar.component(.minute, from: date))"
            }
            
            if seconds.characters.count < 2 {
                seconds = "0\(calendar.component(.second, from: date))"
            }
            
            print("\(hour)\(minutes)\(seconds)")
            
            paramaters["start_time"] = "\(hour)\(minutes)\(seconds)"
            
            // make request to start the game
            Alamofire.request(START_GAME_URL, method: .post, parameters: paramaters).responseJSON { (response) in
                if response.result.isSuccess {
                    let server_data = JSON(response.result.value!)
                    
                    switch server_data["result"].string! {
                    case "success":
                        print("game starting")
                        let game_data = server_data["game"]
                        let target_data = server_data["target"]
                        
                        game?.updateInfo(data: game_data)
                        currentTarget = Player(data: target_data)
                        
                        break
                    default:
                        print("ERROR: server returned result: \(server_data["result"].string!)")
                    }
                } else {
                    print("ERROR: could not connect to server")
                }
            }
            // change button to be stop button
            pressedStart = true
            startStopButton.title = "End Game"
        } else {
            // button is the stop button, stop the game
            var paramaters = [String:String]()
            paramaters["gameid"] = "\(game?.gameID)"
            
            // make request to stop the game
            Alamofire.request(START_GAME_URL, parameters: paramaters).responseJSON { (response) in
                if response.result.isSuccess {
                    let server_data = JSON(response.result.value!)
                    
                    switch server_data["result"].string! {
                    case "success":
                        print("Game Killed")
                        break
                    default:
                        print("ERROR: server returned result: \(server_data["result"].string!)")
                    }
                } else {
                    print("ERROR: could not connect to server")
                }
            }
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        updateTimer = Timer.scheduledTimer(timeInterval: 5, target: self, selector: #selector(updateGame), userInfo: nil, repeats: true)
        gameUpdater = self
        
        if currentUser!.id != game?.hostID {
            startStopButton.isEnabled = false
        }
        
        // Setup Location Manager
        self.locationManager.delegate = self
        self.locationManager.desiredAccuracy = kCLLocationAccuracyBest
        self.locationManager.requestWhenInUseAuthorization()
        self.locationManager.startUpdatingLocation()
        
        // Prepare Map
        mapView.delegate = self
        mapView.userTrackingMode = MKUserTrackingMode(rawValue: 1)!
        mapView.showsUserLocation = true
        mapView.mapType = MKMapType.hybrid
    }
    
    func updateGame() {
        //print("Timer Called")
        // make sure we arent still waiting for data
        if(sendRequests){
            sendRequests = false
            // game is not running so we only wnat to update the player list
            print((game?.isRunning)!)
            if !((game?.isRunning)!) {
                print("Updating Player List")
                updatePlayerList()
            } else {
                print("Updating Game")
                updateFullGame()
            }
        }
    }
    
    func updatePlayerList() {
        var paramaters = [String:String]()
        paramaters["gameid"] = "\(game!.gameID)"
        paramaters["id"] = "\(currentUser!.id)"
        paramaters["x_location"] = "\(Double((center!.longitude)))"
        paramaters["y_location"] = "\(Double((center!.latitude)))"
        
        Alamofire.request(PLAYER_LIST_URL, method: .post, parameters: paramaters).responseJSON { (response) in
            if response.result.isSuccess {
                let server_data = JSON(response.result.value!)
                switch server_data["result"].string! {
                case "normal":
                    game?.player_object_list.removeAll()
                    let num_players = server_data["num_players"].int!
                    // load all players
                    for num in 0...(num_players - 1) {
                        if server_data["Player \(num)"] != JSON.null {
                            let tempPlayer = Player(data: server_data["Player \(num)"])
                            //tempPlayer.printDebugInfo()
                            game?.player_object_list.append(tempPlayer)
                        }
                    }
                    let game_data = server_data["game"]
                    game?.updateInfo(data: game_data)
                    //game?.printPlayerList()
                    self.sendRequests = true
                    break
                default:
                    print("ERROR: server returned result: \(server_data["result"].string!)")
                }
            } else {
                print("ERROR: could not connect to server")
            }
        }
    }
    
    // updates the game once the game is running
    func updateFullGame() {
        var paramaters = [String:String]()
        paramaters["gameid"] = "\(game?.gameID)"
        paramaters["id"] = "\(currentUser!.id)"
        paramaters["x_location"] = "\(Double((center!.longitude)))"
        paramaters["y_location"] = "\(Double((center!.latitude)))"
        
        Alamofire.request(UPDATE_GAME_URL, parameters: paramaters).responseJSON { (response) in
            if response.result.isSuccess {
                let server_data = JSON(response.result.value!)
                
                switch server_data["result"].string! {
                case "normal":
                    game?.player_object_list.removeAll()
                    let num_players = server_data["num_players"].int!
                    // load all players
                    for num in 0...(num_players - 1) {
                        let tempPlayer = Player(data: server_data["Player \(num)"])
                        //tempPlayer.printDebugInfo()
                        game?.player_object_list.append(tempPlayer)
                    }
                    let game_data = server_data["game"]
                    game?.updateInfo(data: game_data)
                    
                    //game?.printPlayerList()
                    self.sendRequests = true
                    break
                default:
                    print("ERROR: server returned result: \(server_data["result"].string!)")
                }
            } else {
                print("ERROR: could not connect to server")
            }
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let location = locations.last
        let center = CLLocationCoordinate2DMake((location?.coordinate.latitude)!, (location?.coordinate.longitude)!)
        let region = MKCoordinateRegion(center: center, span: MKCoordinateSpanMake(0.005, 0.005))
        self.center = center
        mapView.setRegion(region, animated: false)
        playArea = MKCircle(center: center, radius: 100 as CLLocationDistance)
    }
    
    func mapView(_ mapView: MKMapView, rendererFor overlay: MKOverlay) -> MKOverlayRenderer {
        if overlay is MKCircle {
            let circle = MKCircleRenderer(overlay: overlay)
            circle.strokeColor = UIColor.red
            circle.fillColor = UIColor(red: 255, green: 0, blue: 0, alpha: 0.1)
            circle.lineWidth = 1
            return circle
        } else {
            return MKOverlayRenderer(overlay: overlay)
        }
    }
}
