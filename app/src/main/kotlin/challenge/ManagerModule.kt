package challenge

class ManagerModule(repositoryModule: RepositoryModule) {

    val bookingManager = BookingManager.create(repositoryModule.mobilePhoneRepository, InstantFactory.Companion)

}