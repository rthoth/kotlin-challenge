package challenge

import challenge.fonoapi.FonoapiService

class ManagerModule(repositoryModule: RepositoryModule) {

    val bookingManager = BookingManager.create(repositoryModule.mobilePhoneRepository, InstantFactory.Companion)

    val fonoapiService = FonoapiService.create("a-token")

}