package com.peeeq.reciper.details

import com.peeeq.reciper.commons.data.Recipe

class RecipeProvider {
    companion object {
        const val RECIPE_ID = "35120"
        const val RECIPE_RANK = 100.0
        const val RECIPE_TITLE = "Bacon Wrapped Jalapeno Popper Stuffed Chicken"

        @JvmStatic val TEST_RECIPE = Recipe("Closet Cooking",
                RECIPE_TITLE,
                RECIPE_ID,
                RECIPE_RANK,
                arrayOf("4 small chicken breasts, pounded thin",
                        "salt and pepper to taste",
                        "4 jalapenos, diced",
                        "4 ounces cream cheese, room temperature",
                        "1 cup cheddar cheese, shredded",
                        "8 slices bacon\n"),
                "http://static.food2fork.com/Bacon2BWrapped2BJalapeno2BPopper2BStuffed2BChicken2B5002B5909939b0e65.jpg",
                "http://www.closetcooking.com/2012/11/bacon-wrapped-jalapeno-popper-stuffed.html",
                "http://food2fork.com/view/35120")
    }
}