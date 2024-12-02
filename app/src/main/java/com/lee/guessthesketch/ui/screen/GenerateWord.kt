package com.lee.guessthesketch.ui.screen

object StringUtils {
    private val easyStrings = listOf(
        "Sun", "Moon", "Star", "Tree", "Flower", "Cloud", "House", "Car",
        "Book", "Cup", "Apple", "Banana", "Fish", "Cat", "Dog", "Bird",
        "Butterfly", "Boat", "Umbrella", "Balloon", "Chair", "Table", "Shoe", "Hat",
        "Bottle", "Clock", "Pencil", "Brush", "Key", "Lock", "Heart", "Camera",
        "Envelope", "Lamp", "Door", "Window", "Mountain", "Bridge", "Tent", "Bed",
        "Laptop", "Phone", "Tablet", "Glove", "Socks", "Dress", "T-Shirt", "Shorts",
        "Glasses", "Ring", "Candle", "Rocket", "Airplane", "Train", "Bus",
        "Bicycle", "Octopus", "Shell", "Leaf", "Mushroom",
        "Rainbow", "Snowflake", "Ice Cream", "Cake", "Pizza", "Burger", "Fries", "Egg",
        "Cupcake", "Chocolate", "Donut", "Teapot", "Fork", "Knife", "Spoon", "Basket"
    )

    private val mediumStrings = listOf(
        "Guitar", "Violin", "Drum", "Cactus", "Palm Tree", "Lighthouse", "Castle",
        "Horse", "Elephant", "Lion", "Tiger", "Penguin", "Whale", "Dragonfly",
        "Airplane", "Helicopter", "Hot Air Balloon", "Rocket", "Train Engine",
        "Campfire", "Backpack", "Treasure Chest", "Crown", "Helmet", "Spacesuit",
        "Bow and Arrow", "Lantern", "Chess Board", "Dice", "Playing Cards",
        "Surfboard", "Skateboard", "Snowboard", "Tennis Racket", "Soccer Ball",
        "Basketball Hoop", "Volleyball Net", "Fishing Rod", "Parrot", "Owl",
        "Fox", "Wolf", "Swan", "Crab", "Shark", "Dolphin", "Crocodile", "Giraffe",
        "Zebra", "Tortoise", "Peacock", "Kangaroo", "Windmill", "Ferris Wheel",
        "Carousel", "Bridge", "Tent", "Ladder", "Fireplace", "Chimney", "Mailbox",
        "Streetlamp", "Traffic Light", "Park Bench", "Garden Fountain", "Swing Set",
        "Seesaw", "Slide", "Treehouse", "Skyscraper", "Apartment Building",
        "Wind Turbine", "Solar Panel", "Microscope", "Telescope", "Camera Tripod",
        "Film Reel", "Megaphone", "Speaker", "Train Car", "Road Sign", "Clock Tower"
    )

    private val hardStrings = listOf(
        "Accordion", "Ballet Shoes", "Bagpipes", "Banjo", "Barbecue Grill", "Beehive",
        "Binoculars", "Boomerang", "Bucket", "Canoe", "Cannon", "Catapult", "Chainsaw",
        "Compass", "Dartboard", "Dinosaur", "Diving Mask", "Easel", "Flamingo",
        "Frisbee", "Garden Hose", "Giraffe", "Globe", "Gnome", "Goldfish Bowl",
        "Gondola", "Harp", "Hiking Boots", "Igloo", "Jar of Honey", "Kettle",
        "Kite", "Lawnmower", "Loom", "Magnifying Glass", "Microscope", "Monkey",
        "Oil Lamp", "Parachute", "Peacock", "Penguin", "Piggy Bank", "Pineapple",
        "Pitcher", "Plunger", "Pogo Stick", "Rain Boot", "Reindeer", "Rowboat",
        "Saddle", "Seahorse", "Sewing Machine", "Slingshot", "Snow Globe", "Snowmobile",
        "Spinning Top", "Spoon", "Stethoscope", "Suitcase", "Teapot", "Telescope",
        "Thermometer", "Toaster", "Toilet", "Toolbox", "Toucan", "Trampoline",
        "Tripod", "Trumpet", "Umbrella", "Unicycle", "Vacuum Cleaner", "Violin",
        "Watering Can", "Wheelbarrow", "Windmill", "Xylophone", "Yacht", "Zebra"
    )


    fun getRandomString(difficulty: Int): String {
        if (difficulty == 1) {
            val randomIndex = (easyStrings.indices).random()
            return easyStrings[randomIndex]
        }
        if (difficulty == 2) {
            val randomIndex = (mediumStrings.indices).random()
            return mediumStrings[randomIndex]
        }
        if (difficulty == 3) {
            val randomIndex = (hardStrings.indices).random()
            return hardStrings[randomIndex]
        } else {
            return ""
        }
    }
}
